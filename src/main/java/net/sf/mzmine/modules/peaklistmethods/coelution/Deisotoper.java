package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.Feature;

import java.util.*;

class Deisotoper {

    /**
     * The isotopeDistance constant defines expected distance between isotopes.
     * Actual weight of 1 neutron is 1.008665 Da, but part of this mass is
     * consumed as binding energy to other protons/neutrons. Actual mass
     * increase of isotopes depends on chemical formula of the molecule. Since
     * we don't know the formula, we can assume the distance to be ~1.0033 Da,
     * with user-defined tolerance.
     */
    static final double isotopeDistance = 1.0033;

    Deisotoper() {

    }

    HashSet<Feature> deisotope(List<CoelutingGraph> graphs, List<Feature> features, double massDeviationPPM, int maximumCharge) {
        final HashSet<Feature> toDelete = new HashSet<>();
        final HashSet<Feature> done = new HashSet<>();
        for (int i = 0; i < graphs.size(); ++i) {
            final CoelutingGraph graph = graphs.get(i);
            final Feature f = features.get(i);
            if (done.contains(f))
                continue;
            deisotope(graph, massDeviationPPM, maximumCharge);
            for (CoelutingFeature g : graph.vertices.values())
                done.add(g.underlyingFeature);
            for (CoelutingFeature g : graph.getIsotopicFeatures()) {
                toDelete.add(g.underlyingFeature);
            }
        }
        return toDelete;
    }

    /**
     * Detects isotopes in graph,
     *
     * @param graph
     */
    void deisotope(CoelutingGraph graph, double massDeviationPPM, int maximumCharge) {
        final List<CoelutingFeature> features = graph.getNonIsotopicFeatures();
        Collections.sort(features, new Comparator<CoelutingFeature>() {
            @Override
            public int compare(CoelutingFeature o1, CoelutingFeature o2) {
                return Double.compare(o1.underlyingFeature.getMZ(), o2.underlyingFeature.getMZ());
            }
        });
        final ArrayList<CoelutingFeature> isotopes = new ArrayList<>(), buffer = new ArrayList<>();

        for (int k = 0; k < features.size(); ++k) {
            isotopes.clear();
            final CoelutingFeature feature = features.get(k);
            if (feature.isIsotope()) continue;
            int bestChargeState = -1;
            final double featureMz = feature.underlyingFeature.getMZ();
            for (int charge = 1; charge <= maximumCharge; ++charge) {
                buffer.clear();
                buffer.add(feature);
                final double expectedDifference = isotopeDistance / charge;
                final double allowedMassDifference = Math.max(featureMz, 200d) * massDeviationPPM * 1e-6;
                int n = 1;
                int j = k + 1;
                while (j < features.size()) {
                    final CoelutingFeature iso = features.get(j);
                    if (feature.getEdge(iso) != null && iso.getEdge(buffer.get(buffer.size() - 1)) != null) {
                        final double isoMz = iso.underlyingFeature.getMZ();
                        if (Math.abs(isoMz - featureMz - n * expectedDifference) < allowedMassDifference) {
                            buffer.add(iso);
                            ++n;
                        }
                    }
                    ++j;
                }
                if (buffer.size() > isotopes.size()) {
                    bestChargeState = charge;
                    isotopes.clear();
                    isotopes.addAll(buffer);
                }
            }
            if (bestChargeState > 0) {
                // annotate graph
                final Iterator<CoelutingFeature> iter = isotopes.iterator();
                CoelutingFeature source = iter.next();
                int isoN = 0;
                while (iter.hasNext()) {
                    final CoelutingFeature target = iter.next();
                    final CoelutingFeaturePair edge = source.getEdge(target);
                    if (edge.left != source)
                        edge.swapDirection();
                    edge.isotope = ++isoN;
                    source = target;
                }
                // annotate feature
                if (isotopes.size() > 1)
                    feature.underlyingFeature.setIsotopePattern(new CoelutingIsotopePattern(isotopes));
            }
        }


    }

}
