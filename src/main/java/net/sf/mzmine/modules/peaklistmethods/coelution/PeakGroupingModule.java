package net.sf.mzmine.modules.peaklistmethods.coelution;

import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

import javax.annotation.Nonnull;
import java.util.Collection;

public class PeakGroupingModule implements MZmineProcessingModule {
    @Nonnull
    @Override
    public String getName() {
        return "Coeluting Peak Detection";
    }

    @Nonnull
    @Override
    public Class<? extends ParameterSet> getParameterSetClass() {
        return PeakGroupingParameters.class;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Detect coeluting features and deisotope them";
    }

    @Nonnull
    @Override
    public ExitCode runModule(@Nonnull MZmineProject project, @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {
        for (PeakList pl : parameters.getParameter(PeakGroupingParameters.PEAK_LISTS_PARAMETER).getValue().getMatchingPeakLists()) {
            tasks.add(new PeakGroupingTask(project, pl, parameters));
        }
        return ExitCode.OK;
    }

    @Nonnull
    @Override
    public MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.ISOTOPES;
    }
}
