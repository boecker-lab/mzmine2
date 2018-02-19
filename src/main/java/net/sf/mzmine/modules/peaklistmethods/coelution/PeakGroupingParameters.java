package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.MassListParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;

import java.text.NumberFormat;
import java.util.Locale;

public class PeakGroupingParameters extends SimpleParameterSet {

    public static final MassListParameter MASS_LIST = new MassListParameter();
    public static final PeakListsParameter PEAK_LISTS_PARAMETER = new PeakListsParameter();
    public static final BooleanParameter DEISOTOPE = new BooleanParameter("deisotope", "deisotope coeluting peaks", true);

    public static final BooleanParameter REMOVE_NONISOTOPIC_FEATURES = new BooleanParameter("remove features without isotope pattern", "Remove all features without am isotope pattern.");

    public static final StringParameter suffix = new StringParameter(
            "Name suffix", "Suffix to be added to peak list name", "decoeluted");


    public static final DoubleParameter MIN_INTENSITY_FOR_INTERVAL_SELECTION =
            new DoubleParameter("minimal intensity for interval selection",
                    "For each feature cut off both sides of the chromatogram that fall below the given intensity threshold (in relation to the maximum intensity of the feature).", NumberFormat.getPercentInstance(Locale.US), 0.1d, 0d, 1d);

    public static final DoubleParameter MINIMAL_INTENSITY_OVERLAP =
            new DoubleParameter("minimal intensity overlap",
                    "Two features might coelute if the intersection of their retention time intervals covers at least the given percentage of feature intensity.", NumberFormat.getPercentInstance(Locale.US), 0.8d, 0d, 1d);

    public static final DoubleParameter MINIMAL_CORRELATION =
            new DoubleParameter("minimal correlation",
                    "Minimal correlation coefficient for two coeluting features.", NumberFormat.getPercentInstance(Locale.US), 0.7d, 0d, 1d);

    public PeakGroupingParameters() {
        super(new Parameter[]{suffix, PEAK_LISTS_PARAMETER, MASS_LIST, DEISOTOPE, REMOVE_NONISOTOPIC_FEATURES, MIN_INTENSITY_FOR_INTERVAL_SELECTION, MINIMAL_INTENSITY_OVERLAP, MINIMAL_CORRELATION});
    }

}
