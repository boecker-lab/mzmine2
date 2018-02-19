package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.PolarityType;

public class CoelutingPeaks {

    protected final double[] masses;
    protected final double[] intensities;
    protected final double[] correlations;
    protected final Annotation[] annotation;

    public CoelutingPeaks(double[] masses, double[] intensities, double[] correlations, Annotation[] annotation) {
        this.masses = masses;
        this.intensities = intensities;
        this.correlations = correlations;
        this.annotation = annotation;
    }

    public static Annotation unknown(PolarityType polarity, int charge, int isotopes) {
        return new Annotation("[M + ?]" + (charge > 1 ? String.valueOf(charge) : "") + polarity.asSingleChar(), isotopes, true);
    }

    public int size() {
        return masses.length;
    }

    public double getMz(int i) {
        return masses[i];
    }

    public double getIntensity(int i) {
        return intensities[i];
    }

    public double getCorrelation(int i) {
        return correlations[i];
    }

    public Annotation getAnnotation(int i) {
        return annotation[i];
    }

    public static class Annotation {
        protected final String adductType;
        protected final int isotopicPeak;
        protected final boolean unknown;

        public Annotation(String adductType, int isotopicPeak) {
            this(adductType, isotopicPeak, false);
        }

        protected Annotation(String adductType, int isotopicPeak, boolean unknown) {
            this.adductType = adductType;
            this.isotopicPeak = isotopicPeak;
            this.unknown = unknown;
        }

        public boolean isUnknown() {
            return unknown;
        }

        public String getAdductType() {
            return adductType;
        }

        public int getIsotopicPeak() {
            return isotopicPeak;
        }

        public String toString() {
            if (isotopicPeak == 0)
                return adductType;
            else
                return "+" + isotopicPeak;
        }
    }


}
