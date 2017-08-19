package net.sf.mzmine.modules.peaklistmethods.coelution;

public class MzDifference {

    public static MzDifference[] DIFFERENCES_POSITIVE = new MzDifference[]{
            new MzDifference("[M + C2H3N + H]+", "[M + H]+", -41.026549096, 0),
            new MzDifference("[M + K]+", "[M + H]+", -37.955881648, 0),
            new MzDifference("[M + CH4O + H]+", "[M + H]+", -32.026214748, 0),
            new MzDifference("[M + C2H3N + H]+", "[M + H3N + H]+", -23.999999999999993, 0),
            new MzDifference("[M + Na]+", "[M + H]+", -21.981944248, 0),
            new MzDifference("[M + K]+", "[M + H3N + H]+", -20.929332551999998, 0),
            new MzDifference("[M + K]+", "[M + H2O + H]+", -19.945316964, 0),
            new MzDifference("[M + C2H3N + H]+", "[M + Na]+", -19.04460484799999, 0),
            new MzDifference("[M + H2O + H]+", "[M + H]+", -18.010564684, 0),
            new MzDifference("[M + H3N + H]+", "[M + H]+", -17.026549096, 0),
            new MzDifference("[M + K]+", "[M + Na]+", -15.973937399999997, 0),
            new MzDifference("[M + CH4O + H]+", "[M + H3N + H]+", -14.999665651999997, 0),
            new MzDifference("[M + CH4O + H]+", "[M + Na]+", -10.044270499999996, 0),
            new MzDifference("[M + K]+", "[M + CH4O + H]+", -5.929666900000001, 0),
            new MzDifference("[M + Na]+", "[M + H3N + H]+", -4.955395152000001, 0),
            new MzDifference("[M + Na]+", "[M + H2O + H]+", -3.971379564000003, 0),
            new MzDifference("[M + C2H3N + H]+", "[M + K]+", -3.0706674479999947, 0),
            new MzDifference("[M + H2O + H]+", "[M + H3N + H]+", -0.9840155879999983, 0),
            new MzDifference("[M + H3N + H]+", "[M + H2O + H]+", 0.9840155879999983, 0),
            new MzDifference("[M + K]+", "[M + C2H3N + H]+", 3.0706674479999947, 0),
            new MzDifference("[M + H2O + H]+", "[M + Na]+", 3.971379564000003, 0),
            new MzDifference("[M + H3N + H]+", "[M + Na]+", 4.955395152000001, 0),
            new MzDifference("[M + CH4O + H]+", "[M + K]+", 5.929666900000001, 0),
            new MzDifference("[M + Na]+", "[M + CH4O + H]+", 10.044270499999996, 0),
            new MzDifference("[M + H3N + H]+", "[M + CH4O + H]+", 14.999665651999997, 0),
            new MzDifference("[M + Na]+", "[M + K]+", 15.973937399999997, 0),
            new MzDifference("[M + H]+", "[M + H3N + H]+", 17.026549096, 0),
            new MzDifference("[M + H]+", "[M + H2O + H]+", 18.010564684, 0),
            new MzDifference("[M + Na]+", "[M + C2H3N + H]+", 19.04460484799999, 0),
            new MzDifference("[M + H2O + H]+", "[M + K]+", 19.945316964, 0),
            new MzDifference("[M + H3N + H]+", "[M + K]+", 20.929332551999998, 0),
            new MzDifference("[M + H]+", "[M + Na]+", 21.981944248, 0),
            new MzDifference("[M + H3N + H]+", "[M + C2H3N + H]+", 23.999999999999993, 0),
            new MzDifference("[M + H]+", "[M + CH4O + H]+", 32.026214748, 0),
            new MzDifference("[M + H]+", "[M + K]+", 37.955881648, 0),
            new MzDifference("[M + H]+", "[M + C2H3N + H]+", 41.026549096, 0)
    };

    public static MzDifference[] DIFFERENCES_NEGATIVE = new MzDifference[]{
            new MzDifference("[M + C2HF3O2 - H]-", "[M - H]-", -113.992863932, 0),
            new MzDifference("[M + Br]-", "[M - H2O - H]-", -97.936726816, 0),
            new MzDifference("[M + Br]-", "[M - H]-", -79.926162132, 0),
            new MzDifference("[M + C2HF3O2 - H]-", "[M + Cl]-", -78.01618622000001, 0),
            new MzDifference("[M + C2HF3O2 - H]-", "[M + CH2O2 - H]-", -67.987384628, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M - H2O - H]-", -64.016043988, 0),
            new MzDifference("[M + Cl]-", "[M - H2O - H]-", -53.987242396, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M - H]-", -46.005479304, 0),
            new MzDifference("[M + Br]-", "[M + Cl]-", -43.949484420000005, 0),
            new MzDifference("[M + C2H3N - H]-", "[M - H]-", -41.026549096, 0),
            new MzDifference("[M + Br]-", "[M + C2H3N - H]-", -38.899613036000005, 0),
            new MzDifference("[M + Cl]-", "[M - H]-", -35.976677712, 0),
            new MzDifference("[M + C2HF3O2 - H]-", "[M + Br]-", -34.066701800000004, 0),
            new MzDifference("[M + Br]-", "[M + CH2O2 - H]-", -33.920682828000004, 0),
            new MzDifference("[M - H]-", "[M - H2O - H]-", -18.010564684, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M + Cl]-", -10.028801592, 0),
            new MzDifference("[M + C2H3N - H]-", "[M + Cl]-", -5.049871383999999, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M + C2H3N - H]-", -4.978930208000001, 0),
            new MzDifference("[M + C2H3N - H]-", "[M + CH2O2 - H]-", 4.978930208000001, 0),
            new MzDifference("[M + Cl]-", "[M + C2H3N - H]-", 5.049871383999999, 0),
            new MzDifference("[M + Cl]-", "[M + CH2O2 - H]-", 10.028801592, 0),
            new MzDifference("[M - H2O - H]-", "[M - H]-", 18.010564684, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M + Br]-", 33.920682828000004, 0),
            new MzDifference("[M + Br]-", "[M + C2HF3O2 - H]-", 34.066701800000004, 0),
            new MzDifference("[M - H]-", "[M + Cl]-", 35.976677712, 0),
            new MzDifference("[M + C2H3N - H]-", "[M + Br]-", 38.899613036000005, 0),
            new MzDifference("[M - H]-", "[M + C2H3N - H]-", 41.026549096, 0),
            new MzDifference("[M + Cl]-", "[M + Br]-", 43.949484420000005, 0),
            new MzDifference("[M - H]-", "[M + CH2O2 - H]-", 46.005479304, 0),
            new MzDifference("[M - H2O - H]-", "[M + Cl]-", 53.987242396, 0),
            new MzDifference("[M - H2O - H]-", "[M + CH2O2 - H]-", 64.016043988, 0),
            new MzDifference("[M + CH2O2 - H]-", "[M + C2HF3O2 - H]-", 67.987384628, 0),
            new MzDifference("[M + Cl]-", "[M + C2HF3O2 - H]-", 78.01618622000001, 0),
            new MzDifference("[M - H]-", "[M + Br]-", 79.926162132, 0),
            new MzDifference("[M - H2O - H]-", "[M + Br]-", 97.936726816, 0),
            new MzDifference("[M - H]-", "[M + C2HF3O2 - H]-", 113.992863932, 0)
    };

    protected final String left, right;
    protected final double mass;
    protected final int isotopePeak;

    public MzDifference(String left, String right, double mass, int isotopePeak) {
        this.left = left;
        this.right = right;
        this.mass = mass;
        this.isotopePeak = isotopePeak;
    }

    public static double[] getIsotopicMassList(double neutralMz, int maxIsoPeaks) {
        final double[] list = new double[maxIsoPeaks];
        int k = 0;
        for (int i = 1; i <= maxIsoPeaks; ++i) {
            list[k++] = neutralMz + i * Deisotoper.isotopeDistance;
        }
        return list;
    }

    public static double[] getMassList(int polarity, double neutralMz) {
        final MzDifference[] alist = polarity >= 0 ? DIFFERENCES_POSITIVE : DIFFERENCES_NEGATIVE;
        final double[] list = new double[alist.length];
        int k = 0;
        for (MzDifference d : alist) {
            list[k++] = neutralMz + d.mass;
        }
        return list;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public double getMass() {
        return mass;
    }
}
