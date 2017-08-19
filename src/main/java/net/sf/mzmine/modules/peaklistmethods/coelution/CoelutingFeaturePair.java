package net.sf.mzmine.modules.peaklistmethods.coelution;

import com.google.common.collect.Range;

class CoelutingFeaturePair {

    protected int isotope;
    protected CoelutingFeature left, right;
    protected double correlation;
    protected Range<Integer> interval;
    protected double intensityRatioBetweenLeftAndRight;
    protected int numberOfScans;

    CoelutingFeaturePair(CoelutingFeature left, CoelutingFeature right) {
        this.left = left;
        this.right = right;
        this.isotope = 0;
    }

    int getIsotope() {
        return isotope;
    }

    CoelutingFeature getLeft() {
        return left;
    }

    CoelutingFeature getRight() {
        return right;
    }

    double getCorrelation() {
        return correlation;
    }

    Range<Integer> getInterval() {
        return interval;
    }

    double getIntensityRatioBetweenLeftAndRight() {
        return intensityRatioBetweenLeftAndRight;
    }

    int getNumberOfScans() {
        return numberOfScans;
    }

    public String toString() {
        return (isotope <= 0 ? "" : (isotope + "nth isotope between ")) + left.toString() + " -> " + right.toString() + " (" + correlation + " )";
    }

    void swapDirection() {
        CoelutingFeature old = this.left;
        this.left = this.right;
        this.right = old;
        this.intensityRatioBetweenLeftAndRight = 1 / intensityRatioBetweenLeftAndRight;
    }
}
