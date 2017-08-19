package net.sf.mzmine.modules.peaklistmethods.coelution;

import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.Feature;

import java.util.*;

class CorrelationBasedCoelutionFeatureDetector {

    protected double intensityCutoff;
    protected double minimalIntensityOverlap;
    protected double minimalCorrelation;

    protected int idCounter = 0;

    CorrelationBasedCoelutionFeatureDetector(double intensityCutoff, double minimalIntensityOverlap, double minimalCorrelation) {
        this.intensityCutoff = intensityCutoff;
        this.minimalIntensityOverlap = minimalIntensityOverlap;
        this.minimalCorrelation = minimalCorrelation;
    }

    CoelutingFeature addFeature(CoelutingGraph cluster, Feature feature) {
        final CoelutingFeature vertex = new CoelutingFeature(idCounter++, feature);
        vertex.setInterval(getIntensiveRegion(vertex.dataPoints));
        cluster.addVertex(vertex);
        return vertex;
    }

    int detectCoelutingFeatures(CoelutingGraph cluster, CoelutingFeature feature) {
        int count = 0;
        for (CoelutingFeature node : cluster) {
            if (feature.id != node.id && mightCoelute(feature, node)) {
                if (feature.getEdge(node) != null) continue;
                final CoelutingFeaturePair pair = calculateRegionAndOptimalCorrelation(feature, node);
                if (pair != null) {
                    ++count;
                    cluster.addEdge(pair);
                }
            }
        }
        return count;
    }

    void detectFurtherCoelutingFeatures(CoelutingGraph graph, CoelutingFeature feature, double ppm, double[] massList) {
        final boolean[] detected = new boolean[massList.length];
        Arrays.sort(massList);
        for (CoelutingFeature vertex : feature.getNeighbours()) {
            double abs = Math.max(200, vertex.getMedianMz()) * 1e-6 * ppm;
            int i = Arrays.binarySearch(massList, vertex.getMedianMz());
            if (i < 0) i = -i - 1;
            if (i < massList.length && Math.abs(massList[i] - vertex.getMedianMz()) < abs) {
                detected[i] = true;
            } else if (i > 0 && Math.abs(massList[i - 1] - vertex.getMedianMz()) < abs) {
                detected[i - 1] = true;
            }
        }
        final ChromatogramBuilder chr = new ChromatogramBuilder(ppm);
        for (int i = 0; i < massList.length; ++i) {
            if (detected[i]) continue;
            final Feature g = chr.detectFeature(massList[i], feature.interval, feature.getUnderlyingFeature().getRepresentativeScanNumber(), feature.underlyingFeature.getDataFile());
            if (g == null) continue;
            {
                // debug
                for (int scan : g.getScanNumbers()) {
                    if (g.getDataFile().getScan(scan) == null) {
                        System.err.println("wtf?");
                    }
                }
            }
            final CoelutingFeature vertex = new CoelutingFeature(graph.size(), g);
            if (mightCoelute(feature, vertex)) {
                final CoelutingFeaturePair pair = calculateRegionAndOptimalCorrelation(feature, vertex);
                if (pair != null) {
                    graph.addVertex(vertex);
                    graph.addEdge(pair);
                    //System.err.println("Detect new feature: " + feature.getMedianMz() + " m/z @ " + feature.underlyingFeature.getRT() + " min ---> " + vertex.getMedianMz() + " m/z with correlation " + pair.getCorrelation() + " over " + pair.getNumberOfScans() + " scans.");
                }
            }
        }

    }

    void detectCoelutingIsotopes(CoelutingGraph graph, CoelutingFeature feature, double ppm, int isotopes) {
        final ChromatogramBuilder chr = new ChromatogramBuilder(ppm);
        double abs = Math.max(200, feature.getMedianMz()) * 1e-6 * ppm;
        outer:
        for (int charge = 1; charge < 3; ++charge) {
            inner:
            for (int n = 1; n <= isotopes; ++n) {
                final double expectedMz = feature.medianMz + (n * Deisotoper.isotopeDistance / charge);
                boolean found = false;
                for (CoelutingFeature vertex : feature.getNeighbours()) {
                    if (Math.abs(vertex.getMedianMz() - expectedMz) < abs) {
                        found = true;
                        continue inner;
                    }
                }
                if (!found) {
                    final Feature g = chr.detectFeature(expectedMz, feature.interval, feature.getUnderlyingFeature().getRepresentativeScanNumber(), feature.underlyingFeature.getDataFile());
                    if (g == null) break;
                    if (graph.getVertex(g) != null) continue;
                    final CoelutingFeature vertex = new CoelutingFeature(graph.size(), g);
                    if (mightCoelute(feature, vertex)) {
                        final CoelutingFeaturePair pair = calculateRegionAndOptimalCorrelation(feature, vertex);
                        if (pair != null) {
                            graph.addVertex(vertex);
                            graph.addEdge(pair);
                            //System.err.println("Detect new ISOTOPIC feature: " + feature.getMedianMz() + " m/z @ " + feature.underlyingFeature.getRT() + " min ---> " + vertex.getMedianMz() + " m/z with correlation " + pair.getCorrelation() + " over " + pair.getNumberOfScans() + " scans.");
                        }
                    }
                }
            }
        }
    }

    private CoelutingFeaturePair calculateRegionAndOptimalCorrelation(CoelutingFeature left, CoelutingFeature right) {
        final Range<Integer> intersection = left.interval.intersection(right.interval);
        Range<Integer> optimalRegion = null;
        final Iterator<ScanPair> iter = left.foreachScan(right, intersection);
        final List<ScanPair> leftPairs = new ArrayList<>(), rightPairs = new ArrayList<>();
        while (iter.hasNext()) {
            final ScanPair p = iter.next();
            if (p.left == null || p.right == null) return null;
            if (p.left.scanNumber < left.underlyingFeature.getRepresentativeScanNumber()) {
                leftPairs.add(p);
            } else {
                rightPairs.add(p);
            }
        }
        if (leftPairs.size() == 0 || rightPairs.size() <= 1) return null;
        Collections.reverse(rightPairs);
        final ArrayList<ScanPair> selected = new ArrayList<>();
        selected.add(rightPairs.remove(rightPairs.size() - 1));
        selected.add(rightPairs.remove(rightPairs.size() - 1));
        selected.add(leftPairs.remove(leftPairs.size() - 1));
        double correlation = computeCorrelation(selected);
        int nscans = selected.size();
        while (!leftPairs.isEmpty()) {
            ScanPair p = leftPairs.remove(leftPairs.size() - 1);
            selected.add(p);
            double c = computeCorrelation(selected);
            if ((c - 1d / selected.size()) >= (correlation - 1d / nscans)) {
                correlation = c;
                nscans = selected.size();
            } else {
                selected.remove(selected.get(selected.size() - 1));
            }
        }
        while (!rightPairs.isEmpty()) {
            ScanPair p = rightPairs.remove(rightPairs.size() - 1);
            selected.add(p);
            double c = computeCorrelation(selected);
            if ((c - 1d / selected.size()) >= (correlation - 1d / nscans)) {
                nscans = selected.size();
                correlation = c;
            } else {
                selected.remove(selected.get(selected.size() - 1));
            }
        }
        Collections.sort(selected, new Comparator<ScanPair>() {
            @Override
            public int compare(ScanPair o1, ScanPair o2) {
                return Integer.compare(o1.left.scanNumber, o2.left.scanNumber);
            }
        });
        optimalRegion = Range.closed(selected.get(0).left.scanNumber, selected.get(selected.size() - 1).left.scanNumber);
        if ((correlation - 1d / nscans) >= minimalCorrelation) {
            double[] orderedIntensities = new double[selected.size()];
            int k = 0;
            for (ScanPair p : selected) {
                orderedIntensities[k++] = p.left.intensity / p.right.intensity;
            }
            Arrays.sort(orderedIntensities);
            final double median = (orderedIntensities.length % 2 == 0) ? (orderedIntensities[orderedIntensities.length / 2 - 1] + orderedIntensities[orderedIntensities.length / 2]) / 2d : orderedIntensities[orderedIntensities.length / 2];
            final CoelutingFeaturePair pair = new CoelutingFeaturePair(left, right);
            pair.correlation = correlation;
            pair.numberOfScans = nscans;
            pair.intensityRatioBetweenLeftAndRight = median;
            pair.interval = optimalRegion;
            return pair;
        } else return null;
    }

    private double computeCorrelation(List<ScanPair> selected) {
        double expL = 0, expR = 0, varL = 0, varR = 0, covar = 0;
        for (ScanPair p : selected) {
            expL += p.left.intensity;
            expR += p.right.intensity;
        }
        expL /= selected.size();
        expR /= selected.size();
        for (ScanPair p : selected) {
            varL += Math.pow(p.left.intensity - expL, 2);
            varR += Math.pow(p.right.intensity - expR, 2);
            covar += (p.left.intensity - expL) * (p.right.intensity - expR);
        }
        return covar / (Math.sqrt(varL) * Math.sqrt(varR));
    }


    protected boolean mightCoelute(CoelutingFeature left, CoelutingFeature right) {
        if (!left.interval.isConnected(right.interval))
            return false;
        // 80% of their intensities should overlap
        double sharedIntensityL = 0d, sharedIntensityR = 0d;
        double unionIntensityL = 0d, unionIntensityR = 0d;
        final Iterator<ScanPair> i = left.foreachScan(right, left.interval.span(right.interval));
        while (i.hasNext()) {
            ScanPair p = i.next();
            if (p.left != null) {
                unionIntensityL += p.left.intensity;
                if (p.right != null) sharedIntensityL += p.left.intensity;
            }
            if (p.right != null) {
                unionIntensityR += p.right.intensity;
                if (p.left != null) sharedIntensityR += p.right.intensity;
            }
        }
        return sharedIntensityL / unionIntensityL >= minimalIntensityOverlap && sharedIntensityR / unionIntensityR >= minimalIntensityOverlap;

    }


    protected Range<Integer> getIntensiveRegion(ScanDataPoint[] scans) {
        double maxIntensity = 0;
        int maxIntensityIndex = 0;
        for (int i = 0; i < scans.length; ++i) {
            final ScanDataPoint p = scans[i];
            if (p.intensity > maxIntensity) {
                maxIntensity = p.intensity;
                maxIntensityIndex = i;
            }
        }
        int start, end;
        for (start = maxIntensityIndex - 1; start >= 0; --start) {
            if (scans[start].intensity / maxIntensity < intensityCutoff) {
                break;
            }
        }
        ++start;
        for (end = maxIntensityIndex + 1; end < scans.length; ++end) {
            if (scans[end].intensity / maxIntensity < intensityCutoff) break;
        }
        --end;

        return Range.closed(scans[start].scanNumber, scans[end].scanNumber);

    }


}
