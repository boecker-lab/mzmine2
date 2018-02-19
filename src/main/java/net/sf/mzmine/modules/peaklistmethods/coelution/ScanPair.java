package net.sf.mzmine.modules.peaklistmethods.coelution;

class ScanPair {
    protected final ScanDataPoint left, right;

    ScanPair(ScanDataPoint left, ScanDataPoint right) {
        this.left = left;
        this.right = right;
    }

}