package net.sf.mzmine.modules.peaklistmethods.coelution;


import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.datamodel.impl.SimplePeakList;
import net.sf.mzmine.datamodel.impl.SimplePeakListAppliedMethod;
import net.sf.mzmine.datamodel.impl.SimplePeakListRow;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

import java.util.*;

public class PeakGroupingTask extends AbstractTask {

    private final MZmineProject project;
    protected PeakList peakList;
    protected String massListName;
    protected double cutoffAt, minimalIntensityOverlap, minimalCorrelation;
    protected volatile double progress;
    protected boolean deisotope, removeNonIsotopes;
    protected String suffix;
    protected ParameterSet parameters;

    public PeakGroupingTask(MZmineProject project, PeakList peakList, ParameterSet parameters) {
        this.progress = 0d;
        this.deisotope = parameters.getParameter(PeakGroupingParameters.DEISOTOPE).getValue();
        this.cutoffAt = parameters.getParameter(PeakGroupingParameters.MIN_INTENSITY_FOR_INTERVAL_SELECTION).getValue();
        this.minimalIntensityOverlap = parameters.getParameter(PeakGroupingParameters.MINIMAL_INTENSITY_OVERLAP).getValue();
        this.minimalCorrelation = parameters.getParameter(PeakGroupingParameters.MINIMAL_CORRELATION).getValue();
        this.massListName = parameters.getParameter(PeakGroupingParameters.MASS_LIST).getValue();
        this.suffix = parameters.getParameter(PeakGroupingParameters.suffix).getValue();
        this.peakList = peakList;
        this.project = project;
        this.parameters = parameters;
        this.removeNonIsotopes = parameters.getParameter(PeakGroupingParameters.REMOVE_NONISOTOPIC_FEATURES).getValue();
    }

    @Override
    public String getTaskDescription() {
        return "Detect groups of peaks that overlap in their retention time intervals and have correlated intensities. These peaks are good candidates for coeluting compounds, isotope patterns, in-source fragments, and adducts.";
    }

    @Override
    public double getFinishedPercentage() {
        return progress;
    }

    @Override
    public void run() {

        this.progress = 0d;

        // estimate number of features
        final int nfeatures;
        {
            int peaks = 0;
            for (PeakListRow pr : peakList.getRows()) {
                peaks += pr.getNumberOfPeaks();
            }
            nfeatures = peaks;
        }

        int numberOfProcessedFeatures = 0;
        final CorrelationBasedCoelutionFeatureDetector detector = new CorrelationBasedCoelutionFeatureDetector(cutoffAt, minimalIntensityOverlap, minimalCorrelation);

        final PeakList copy = new SimplePeakList(peakList.getName() + " " + suffix, peakList.getRawDataFiles());
        final ArrayList<PeakListRow> crows = new ArrayList<>();

        final CoelutingGraph clusters = new CoelutingGraph();
        if (peakList.getRawDataFiles().length > 1) {
            setErrorMessage("Your peaklists stem from more than one file and, maybe, from more than one sample or replicate. It is highly recommended to do the peak grouping BEFORE aligning the different samples/replicates.");
        }
        for (RawDataFile raw : peakList.getRawDataFiles()) {
            for (PeakListRow row : peakList.getRows()) {
                final PeakListRow copyRow = new SimplePeakListRow(row.getID());
                final Feature feature = row.getPeak(raw);
                if (feature == null || feature.getFeatureStatus() != Feature.FeatureStatus.DETECTED) {
                    if (feature != null) copyRow.addPeak(raw, feature);
                    continue;
                }
                final CoelutingFeature node = detector.addFeature(clusters, feature);
                if (node != null) {
                    detector.detectCoelutingFeatures(clusters, node);
                }
                ++numberOfProcessedFeatures;
                this.progress = numberOfProcessedFeatures / (double) nfeatures;
                copyRow.addPeak(raw, feature);
                crows.add(copyRow);
            }
            for (CoelutingFeature node : clusters.getFeatures()) {
                final PolarityType polarity = raw.getScan(node.getUnderlyingFeature().getRepresentativeScanNumber()).getPolarity();
                // add adduct features
                detector.detectFurtherCoelutingFeatures(clusters, node, 15d, MzDifference.getMassList(polarity.getSign(), node.getMedianMz()));
            }
            // add isotopic features
            for (CoelutingFeature node : clusters.getFeatures()) {
                detector.detectCoelutingIsotopes(clusters, node, 15d, 5);
            }
            // add missing edges
            for (CoelutingFeature node : clusters.getFeatures()) {
                detector.detectCoelutingFeatures(clusters, node);
            }
        }

        final List<CoelutingGraph> graphs = clusters.getConnectionComponents();
        final Deisotoper deisotoper = new Deisotoper();
        for (CoelutingGraph g : graphs) {
            deisotoper.deisotope(g, 20, 3);
            for (CoelutingFeature f : g) {
                final PolarityType polarity = f.getUnderlyingFeature().getDataFile().getScan(f.getUnderlyingFeature().getRepresentativeScanNumber()).getPolarity();
                Feature feature = f.getUnderlyingFeature();
                annotate(feature, f, polarity);
            }
        }
        if (deisotope) {
            final HashSet<Feature> toDelete = new HashSet<>();
            for (CoelutingGraph g : graphs) {
                for (CoelutingFeature f : g.getIsotopicFeatures())
                    toDelete.add(f.getUnderlyingFeature());
                if (removeNonIsotopes) {
                    for (CoelutingFeature f : g.getNonIsotopicFeatures()) {
                        if (!f.hasIsotopes())
                            toDelete.add(f.getUnderlyingFeature());
                    }
                }
                for (CoelutingFeature f : g.getNonIsotopicFeatures()) {
                    final IsotopePattern pattern = f.getUnderlyingFeature().getIsotopePattern();
                    if (pattern != null && pattern.getDataPoints().length > 1) {
                        f.getUnderlyingFeature().setCharge(determineChargeFromIsotopePattern(pattern));
                    }
                }

            }
            final Iterator<PeakListRow> plIter = crows.iterator();
            while (plIter.hasNext()) {
                if (toDelete.contains(plIter.next().getPeaks()[0])) {
                    plIter.remove();
                }
            }
        }
        for (PeakListRow pr : crows) {
            copy.addRow(pr);
        }
        // Add new peakList to the project
        project.addPeakList(copy);

        // Load previous applied methods
        for (PeakList.PeakListAppliedMethod proc : peakList.getAppliedMethods()) {
            copy.addDescriptionOfAppliedTask(proc);
        }

        // Add task description to peakList
        copy
                .addDescriptionOfAppliedTask(new SimplePeakListAppliedMethod(
                        "Isotopic peaks grouper", parameters));

        setStatus(TaskStatus.FINISHED);

    }

    private void annotate(Feature feature, CoelutingFeature f, PolarityType polarity) {

        final MzDifference[] diffs = polarity == PolarityType.NEGATIVE ? MzDifference.DIFFERENCES_NEGATIVE : MzDifference.DIFFERENCES_POSITIVE;
        final List<List<CoelutingFeature>> adductsWithIsotopes = new ArrayList<>();
        final List<List<MzDifference>> adductTypes = new ArrayList<>();
        final List<Integer> charges = new ArrayList<>();
        final double mz = f.getMedianMz();
        final double allowedDifference = 15e-6 * mz;
        final int mainCharge = getCharge(f.getIsotopes());

        final HashMap<String, Integer> simpleScoring = new HashMap<>();

        charges.add(mainCharge);
        adductsWithIsotopes.add(f.getIsotopes());
        adductTypes.add(null);

        for (CoelutingFeature v : f.getNeighbours()) {
            if (v.isIsotope()) continue;
            final List<CoelutingFeature> isotopes = v.getIsotopes();
            final int neighbourCharge = getCharge(isotopes);
            charges.add(neighbourCharge);
            adductsWithIsotopes.add(isotopes);
            final List<MzDifference> found = new ArrayList<>();
            adductTypes.add(found);
            int score = isotopes.size() > 1 ? 1 + isotopes.size() : 1;
            if (mainCharge <= 1 && neighbourCharge <= 1) { // currently we only support single charged annotation
                for (MzDifference diff : diffs) {
                    if (Math.abs(diff.getMass() - (v.getMedianMz() - mz)) < allowedDifference) {
                        found.add(diff);
                        if (simpleScoring.containsKey(diff.left))
                            simpleScoring.put(diff.left, simpleScoring.get(diff.left) + score);
                        else
                            simpleScoring.put(diff.left, score);
                    }
                }
            }
        }

        // decide for most likely annotation or none if it is unclear
        int maxScore = 0;
        String mostLikely = null;
        for (Integer val : simpleScoring.values()) maxScore = Math.max(maxScore, val);
        boolean unclear = maxScore <= 0;
        for (String key : simpleScoring.keySet()) {
            if (simpleScoring.get(key) == maxScore) {
                if (mostLikely != null) {
                    unclear = true;
                    break;
                }
                mostLikely = key;
            } else if (simpleScoring.get(key) + 1 >= maxScore) {
                unclear = true;
            }
        }
        int k = 0;
        for (List<CoelutingFeature> x : adductsWithIsotopes) k += x.size();
        final double[] mzValues = new double[k];
        final double[] intValues = new double[k];
        final double[] corValues = new double[k];
        final CoelutingPeaks.Annotation[] annotations = new CoelutingPeaks.Annotation[k];
        k = 0;
        outer:
        for (int i = 0; i < adductsWithIsotopes.size(); ++i) {
            final List<MzDifference> at = adductTypes.get(i);
            final List<CoelutingFeature> iso = adductsWithIsotopes.get(i);
            final int charge = charges.get(i);
            for (int j = k; j < iso.size() + k; ++j) {
                mzValues[j] = iso.get(j - k).getMedianMz();
                if (f.getEdge(iso.get(j - k)) != null) {
                    intValues[j] = f.getIntensityRelativeTo(iso.get(j - k));
                    corValues[j] = f.getEdge(iso.get(j - k)).getCorrelation();
                } else {
                    intValues[j] = f.getIntensityRelativeTo(iso.get(0)) * iso.get(0).getIntensityRelativeTo(iso.get(j - k));
                    corValues[j] = 0d;
                }
            }
            if (!unclear) {
                if (i == 0) {
                    for (int is = 0; is < iso.size(); ++is)
                        annotations[k++] = new CoelutingPeaks.Annotation(mostLikely, is);
                    continue outer;
                } else {
                    for (MzDifference d : at) {
                        if (d.left.equals(mostLikely)) {
                            for (int is = 0; is < iso.size(); ++is)
                                annotations[k++] = new CoelutingPeaks.Annotation(d.right, is);
                            continue outer;
                        }
                    }
                }
            }
            for (int is = 0; is < iso.size(); ++is)
                annotations[k++] = CoelutingPeaks.unknown(polarity, charge, is);
        }
        feature.set(CoelutingPeaks.class, new CoelutingPeaks(mzValues, intValues, corValues, annotations));


    }

    private int getCharge(List<CoelutingFeature> isotopePattern) {
        {
            double chargeState = 0d;
            for (int k = 1; k < isotopePattern.size(); ++k) {
                chargeState += isotopePattern.get(k).getMedianMz() - isotopePattern.get(k - 1).getMedianMz();
            }
            if (chargeState > 0) {
                chargeState /= (isotopePattern.size() - 1);
                return (int) Math.round(Deisotoper.isotopeDistance / chargeState);
            } else {
                return 1;
            }
        }
    }

    private int determineChargeFromIsotopePattern(IsotopePattern pattern) {
        final DataPoint[] ps = pattern.getDataPoints();
        double diff = 0d;
        for (int i = 1; i < ps.length; ++i) {
            diff += ps[i].getMZ() - ps[i - 1].getMZ();
        }
        diff /= (ps.length - 1);
        return (int) Math.round(Deisotoper.isotopeDistance / diff);
    }
}
