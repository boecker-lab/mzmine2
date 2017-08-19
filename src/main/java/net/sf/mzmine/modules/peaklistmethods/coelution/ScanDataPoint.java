package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.DataPoint;

class ScanDataPoint implements Comparable<ScanDataPoint>, DataPoint {
    protected final double mz;
    protected final double intensity;
    protected final int scanNumber;

    ScanDataPoint(int scanNumber, DataPoint dp) {
        this(scanNumber, dp.getMZ(), dp.getIntensity());
    }

    ScanDataPoint(int scanNumber, double mz, double intensity) {
        this.mz = mz;
        this.intensity = intensity;
        this.scanNumber = scanNumber;
    }

    @Override
    public int compareTo(ScanDataPoint o) {
        return Integer.compare(scanNumber, o.scanNumber);
    }

    @Override
    public double getMZ() {
        return mz;
    }

    @Override
    public double getIntensity() {
        return intensity;
    }
}
