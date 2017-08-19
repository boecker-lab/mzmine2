package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Cluster of coeluting compounds
 */
class CoelutingGraph implements Iterable<CoelutingFeature> {

    protected HashMap<Integer, CoelutingFeature> vertices;

    CoelutingGraph() {
        this.vertices = new HashMap<>();
    }

    CoelutingFeature getVertex(Feature f) {
        for (CoelutingFeature g : this) {
            if (g.underlyingFeature.equals(f) || (Math.abs(g.underlyingFeature.getMZ() - f.getMZ()) < 1e-6 && Math.abs(g.underlyingFeature.getRT() - f.getRT()) < 1e-3))
                return g;
        }
        return null;
    }

    List<CoelutingGraph> getConnectionComponents() {

        final HashMap<Integer, Integer> colors = new HashMap<>();
        final ArrayList<CoelutingGraph> graphs = new ArrayList<>();

        final Iterator<CoelutingFeature> iter = iterator();
        while (iter.hasNext()) {
            final CoelutingFeature init = iter.next();
            if (!colors.containsKey(init.id)) {
                CoelutingGraph g = new CoelutingGraph();
                int gid = graphs.size();
                graphs.add(g);
                spread(g, gid, init, colors);
            }
        }
        return graphs;
    }

    List<CoelutingFeature> getNonIsotopicFeatures() {
        final ArrayList<CoelutingFeature> features = new ArrayList<>();
        for (CoelutingFeature f : this) {
            if (!f.isIsotope()) {
                features.add(f);
            }
        }
        return features;
    }

    private void spread(CoelutingGraph g, int graphColor, CoelutingFeature node, HashMap<Integer, Integer> colors) {
        colors.put(node.id, graphColor);
        final ArrayList<CoelutingFeature> stack = new ArrayList<>();
        stack.add(node);
        while (!stack.isEmpty()) {
            node = stack.remove(stack.size() - 1);
            g.addVertex(node);
            for (CoelutingFeature neighbour : node.getNeighbours()) {
                if (!colors.containsKey(neighbour.id)) {
                    stack.add(neighbour);
                    colors.put(neighbour.id, graphColor);
                }
            }
        }
    }

    void addVertex(CoelutingFeature feature) {
        this.vertices.put(feature.id, feature);
    }

    void addEdge(CoelutingFeaturePair pair) {
        vertices.get(pair.left.id).edges.add(pair);
        vertices.get(pair.right.id).edges.add(pair);
    }

    int size() {
        return vertices.size();
    }

    @Override
    public Iterator<CoelutingFeature> iterator() {
        return vertices.values().iterator();
    }

    List<CoelutingFeature> getIsotopicFeatures() {
        final ArrayList<CoelutingFeature> features = new ArrayList<>();
        for (CoelutingFeature f : this) {
            if (f.isIsotope()) features.add(f);
        }
        return features;
    }

    public String toString() {
        return vertices.values().toString();
    }

    List<CoelutingFeature> getFeatures() {
        return new ArrayList<>(vertices.values());
    }
}
