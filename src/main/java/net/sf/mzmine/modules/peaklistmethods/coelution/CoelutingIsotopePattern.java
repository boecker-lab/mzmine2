package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleIsotopePattern;

import java.util.List;


class CoelutingIsotopePattern extends SimpleIsotopePattern {

    CoelutingIsotopePattern(List<CoelutingFeature> features) {
        super(extractDataPoints(features), IsotopePatternStatus.DETECTED, "");
    }

    private static DataPoint[] extractDataPoints(List<CoelutingFeature> features) {
        final DataPoint[] points = new DataPoint[features.size()];
        final CoelutingFeature monoisotopic = features.get(0);
        final double[] mz = new double[features.size()];
        final double[] intensity = new double[features.size()];
        mz[0] = monoisotopic.getMedianMz();
        intensity[0] = 1d;
        double totalIntensity = 1d;
        for (int k = 1; k < features.size(); ++k) {
            mz[k] = features.get(k).getMedianMz();
            intensity[k] = 1d / monoisotopic.getEdge(features.get(k)).intensityRatioBetweenLeftAndRight;
            totalIntensity += intensity[k];
        }
        for (int k = 0; k < points.length; ++k) {
            points[k] = new SimpleDataPoint(mz[k], intensity[k] / totalIntensity);
        }
        return points;
    }
}
