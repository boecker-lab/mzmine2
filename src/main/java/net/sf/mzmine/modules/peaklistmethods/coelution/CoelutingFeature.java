package net.sf.mzmine.modules.peaklistmethods.coelution;


import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Feature;

import java.util.*;

class CoelutingFeature {
    protected final int id;
    protected final ScanDataPoint[] dataPoints;
    protected Feature underlyingFeature;
    protected double medianMz = -1d;
    protected List<CoelutingFeaturePair> edges;
    protected Range<Integer> interval;

    CoelutingFeature(int id, Feature feature) {
        this.underlyingFeature = feature;
        this.edges = new ArrayList<>();
        this.dataPoints = getDatapoints(feature);
        this.interval = Range.closed(dataPoints[0].scanNumber, dataPoints[dataPoints.length - 1].scanNumber);
        this.id = id;
    }

    CoelutingFeature(CoelutingFeature feature) {
        this.id = feature.id;
        this.edges = new ArrayList<>(feature.edges);
        this.dataPoints = feature.dataPoints;
        this.interval = feature.interval;
        this.medianMz = feature.medianMz;
        this.underlyingFeature = feature.underlyingFeature;
    }

    void setInterval(Range<Integer> range) {
        medianMz = -1d;
        this.interval = range;
    }

    double getIntensityRelativeTo(CoelutingFeature other) {
        if (other == this) return 1d;
        final CoelutingFeaturePair pair = getEdge(other);
        if (pair == null) {
            final HashMap<Integer, Double> map = new HashMap<>();
            final ArrayDeque<CoelutingFeature> stack = new ArrayDeque<>();
            stack.add(this);
            map.put(id, 1d);
            while (!stack.isEmpty()) {
                final CoelutingFeature node = stack.removeFirst();
                for (CoelutingFeaturePair f : node.edges) {
                    final CoelutingFeature otherFeature = f.left == node ? f.right : f.left;
                    if (!map.containsKey(otherFeature.id)) {
                        stack.add(otherFeature);
                        map.put(otherFeature.id, f.left == otherFeature ? map.get(node.id) * f.getIntensityRatioBetweenLeftAndRight() : map.get(node.id) / f.getIntensityRatioBetweenLeftAndRight());
                        if (otherFeature.equals(other)) return map.get(otherFeature.id);
                    }
                }
            }
            return 0d;
        } else if (pair.left == other) {
            return pair.getIntensityRatioBetweenLeftAndRight();
        } else return 1d / pair.getIntensityRatioBetweenLeftAndRight();
    }

    protected Iterator<ScanPair> foreachScan(CoelutingFeature other, Range<Integer> usedInterval) {
        return new ScanPairIterator(dataPoints, other.dataPoints, usedInterval);
    }

    private ScanDataPoint[] getDatapoints(Feature f) {
        final int[] scans = f.getScanNumbers();
        final ScanDataPoint[] points = new ScanDataPoint[scans.length];
        int k = 0;
        for (int scan : scans) {
            DataPoint p = f.getDataPoint(scan);
            points[k++] = new ScanDataPoint(scan, p);
        }
        Arrays.sort(points);
        return points;
    }


    CoelutingFeature[] getNeighbours() {
        final CoelutingFeature[] nbs = new CoelutingFeature[edges.size()];
        for (int k = 0; k < edges.size(); ++k) {
            final CoelutingFeaturePair pair = edges.get(k);
            if (pair.left == this) nbs[k] = pair.right;
            else nbs[k] = pair.left;
        }
        return nbs;
    }

    CoelutingFeaturePair getEdge(CoelutingFeature target) {
        for (CoelutingFeaturePair pair : edges)
            if (pair.left == target || pair.right == target)
                return pair;
        return null;
    }

    boolean isIsotope() {
        for (CoelutingFeaturePair pair : edges)
            if (pair.right == this && pair.isotope > 0) return true;
        return false;
    }

    double getMedianMz() {
        if (medianMz >= 0) return medianMz;
        else {
            final double[] mz = new double[dataPoints.length];
            for (int k = 0; k < dataPoints.length; ++k)
                mz[k] = dataPoints[k].mz;
            Arrays.sort(mz);
            medianMz = mz.length % 2 == 0 ? (mz[mz.length / 2] + mz[mz.length / 2 - 1]) / 2d : mz[mz.length / 2];
            return medianMz;
        }
    }

    Feature getUnderlyingFeature() {
        return underlyingFeature;
    }

    boolean hasIsotopes() {
        for (CoelutingFeaturePair edge : edges) {
            if (edge.left == this && edge.isotope > 0) return true;
        }
        return false;
    }

    List<CoelutingFeature> getIsotopes() {
        final ArrayList<CoelutingFeature> list = new ArrayList<>();
        list.add(this);
        each:
        while (true) {
            final CoelutingFeature n = list.get(list.size() - 1);
            for (CoelutingFeaturePair e : n.edges) {
                if (e.left == n && e.isotope > 0) {
                    list.add(e.right);
                    continue each;
                }
            }
            break;
        }
        return list;
    }

    public String toString() {
        return String.valueOf(getMedianMz()) + " @ " + interval.toString();
    }
}
