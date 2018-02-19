package net.sf.mzmine.modules.peaklistmethods.coelution;

import com.google.common.collect.Range;

import java.util.Iterator;

class ScanPairIterator implements Iterator<ScanPair> {
    protected final ScanDataPoint[] left, right;
    protected final int leftLength, rightLenth;
    protected int i, j;

    ScanPairIterator(ScanDataPoint[] left, ScanDataPoint[] right, Range<Integer> interval) {
        this.left = left;
        this.right = right;
        this.i = 0;
        this.j = 0;
        while (i < left.length) {
            if (left[i].scanNumber >= interval.lowerEndpoint()) break;
            else ++i;
        }
        while (j < right.length) {
            if (right[j].scanNumber >= interval.lowerEndpoint()) break;
            else ++j;
        }
        int lastL = left.length - 1, lastR = right.length - 1;
        while (lastL >= 0) {
            if (left[lastL].scanNumber <= interval.upperEndpoint()) {
                break;
            } else --lastL;
        }
        while (lastR >= 0) {
            if (right[lastR].scanNumber <= interval.upperEndpoint()) {
                break;
            } else --lastR;
        }
        this.leftLength = lastL + 1;
        this.rightLenth = lastR + 1;
    }

    @Override
    public boolean hasNext() {
        return i < leftLength && j < rightLenth;
    }

    @Override
    public ScanPair next() {
        ScanDataPoint l = i < leftLength ? left[i] : null;
        ScanDataPoint r = j < rightLenth ? right[j] : null;
        if (l != null) {
            if (r == null || l.scanNumber < r.scanNumber) {
                ++i;
                return new ScanPair(l, null);
            } else if (l.scanNumber == r.scanNumber) {
                ++i;
                ++j;
                return new ScanPair(l, r);
            } else {
                ++j;
                return new ScanPair(null, r);
            }
        } else {
            ++j;
            return new ScanPair(null, r);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}