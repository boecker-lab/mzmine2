package net.sf.mzmine.modules.peaklistmethods.coelution;

import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.datamodel.impl.SimpleFeature;

import java.util.*;

class ChromatogramBuilder {

    protected double ppm;

    ChromatogramBuilder(double ppm) {
        this.ppm = ppm;
    }

    Feature detectFeature(double featureMz, Range<Integer> interval, int mostIntensiveScan, RawDataFile rawScans) {
        final int[] scanNumbers = rawScans.getScanNumbers(1);
        Arrays.sort(scanNumbers);
        int i = Arrays.binarySearch(scanNumbers, interval.lowerEndpoint());
        if (i < 0) i = -i - 1;

        ArrayDeque<ScanDataPoint> dataPoints = new ArrayDeque<>();
        // start with most intensive scan
        DataPoint dp = findDatapoint(featureMz, rawScans, mostIntensiveScan, ppm);
        if (dp == null) return null;
        dataPoints.addFirst(new ScanDataPoint(mostIntensiveScan, dp));
        double newMz = dp.getMZ();
        int middle = Arrays.binarySearch(scanNumbers, mostIntensiveScan);
        if (middle < 0) throw new IllegalArgumentException("Most intensive scan not contained in raw data file");
        for (int j = middle + 1; j < scanNumbers.length && scanNumbers[j] < interval.upperEndpoint(); ++j) {
            final DataPoint point = findDatapoint(newMz, rawScans, scanNumbers[j], Math.max(5, ppm / 2d));
            if (point != null) {
                dataPoints.addLast(new ScanDataPoint(scanNumbers[j], point));
                newMz = point.getMZ();
            } else break;
        }
        newMz = dp.getMZ();
        for (int j = middle - 1; j >= i; --j) {
            final DataPoint point = findDatapoint(newMz, rawScans, scanNumbers[j], Math.max(5, ppm / 2d));
            if (point != null) {
                dataPoints.addFirst(new ScanDataPoint(scanNumbers[j], point));
                newMz = point.getMZ();
            } else break;
        }

        if (dataPoints.size() < 3)
            return null;

        final ArrayList<ScanDataPoint> dps = new ArrayList<>(dataPoints);
        Collections.sort(dps, new Comparator<ScanDataPoint>() {
            @Override
            public int compare(ScanDataPoint o1, ScanDataPoint o2) {
                return Double.compare(o1.mz, o2.mz);
            }
        });
        final int[] scans = new int[dataPoints.size()];
        int k = 0;
        for (ScanDataPoint p : dataPoints) scans[k++] = p.scanNumber;
        final double medianMz = dps.size() % 2 == 0 ? (dps.get(dps.size() / 2).mz + dps.get(dps.size() / 2 - 1).mz) / 2d : dps.get(dps.size() / 2).mz;

        double minIntensity = Double.POSITIVE_INFINITY, maxIntensity = Double.NEGATIVE_INFINITY;
        for (ScanDataPoint p : dataPoints) {
            maxIntensity = Math.max(p.getIntensity(), maxIntensity);
            minIntensity = Math.min(p.getIntensity(), minIntensity);
        }


        return new SimpleFeature(rawScans, medianMz, rawScans.getScan(mostIntensiveScan).getRetentionTime(), dp.getIntensity(), 0d, scans, dataPoints.toArray(new DataPoint[dataPoints.size()]), Feature.FeatureStatus.ESTIMATED, mostIntensiveScan, -1, Range.closed(rawScans.getScan(scans[0]).getRetentionTime(), rawScans.getScan(scans[scans.length - 1]).getRetentionTime()), Range.closed(dps.get(0).getMZ(), dps.get(dps.size() - 1).getMZ()), Range.closed(minIntensity, maxIntensity));
    }

    private DataPoint findDatapoint(double featureMz, RawDataFile rawScans, int mostIntensiveScan, double ppm) {
        final Scan scan = rawScans.getScan(mostIntensiveScan);
        if (scan.getSpectrumType() == MassSpectrumType.PROFILE) {
            throw new RuntimeException("Chromatogram builder does not work on profiled spectra");
        }
        // estimate noise level
        TreeSet<Double> noisePeaks = new TreeSet<>();
        double threshold = Double.POSITIVE_INFINITY;
        final DataPoint[] dataPoints = scan.getDataPoints();
        int quantile5 = Math.min(2, (int) Math.floor(dataPoints.length * 0.05));
        final double allowedDifference = Math.max(featureMz, 200) * 1e-6 * ppm;
        final double from = featureMz - allowedDifference, to = featureMz + allowedDifference;
        DataPoint mostIntensive = null;
        for (DataPoint p : dataPoints) {
            if (p.getMZ() >= from && p.getMZ() < to && (mostIntensive == null || mostIntensive.getIntensity() < p.getIntensity())) {
                mostIntensive = p;
            }
            if (p.getIntensity() < threshold) {
                noisePeaks.add(p.getIntensity());
                if (noisePeaks.size() >= quantile5) {
                    Iterator<Double> iter = noisePeaks.descendingIterator();
                    iter.next();
                    iter.remove();
                    threshold = iter.next();
                }
            }
        }
        if (mostIntensive != null && mostIntensive.getIntensity() > threshold) {
            return mostIntensive;
        } else return null;
    }
}
