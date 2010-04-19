/*
 * Copyright 2006-2010 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.twod;

import java.text.NumberFormat;

import net.sf.mzmine.data.ChromatographicPeak;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.PeakListRow;
import net.sf.mzmine.main.MZmineCore;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

/**
 * Tooltip generator for 2D visualizer
 */
class PeakToolTipGenerator implements XYToolTipGenerator {

    private NumberFormat rtFormat = MZmineCore.getRTFormat();
    private NumberFormat mzFormat = MZmineCore.getMZFormat();
    private NumberFormat intensityFormat = MZmineCore.getIntensityFormat();

    /**
     * @see org.jfree.chart.labels.XYToolTipGenerator#generateToolTip(org.jfree.data.xy.XYDataset,
     *      int, int)
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {

        PeakDataSet peakDataSet = (PeakDataSet) dataset;
        PeakDataPoint dataPoint = peakDataSet.getDataPoint(series, item);

        PeakList peakList = peakDataSet.getPeakList();
        ChromatographicPeak peak = peakDataSet.getPeak(series);
        PeakListRow row = peakList.getPeakRow(peak);
        double rtValue = dataPoint.getRT();
        double intValue = dataPoint.getIntensity();
        double mzValue = dataPoint.getMZ();
        int scanNumber = dataPoint.getScanNumber();

        String toolTip = "Peak: " + peak + "\nStatus: "
                + peak.getPeakStatus() + "\nPeak list row: " + row
                + "\nScan #" + scanNumber + "\nRetention time: "
                + rtFormat.format(rtValue) + "\nm/z: "
                + mzFormat.format(mzValue) + "\nIntensity: "
                + intensityFormat.format(intValue);

        return toolTip;
    }

}
