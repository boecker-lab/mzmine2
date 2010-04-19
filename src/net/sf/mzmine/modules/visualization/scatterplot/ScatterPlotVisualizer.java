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

package net.sf.mzmine.modules.visualization.scatterplot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.mzmine.data.ParameterSet;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.desktop.MZmineMenu;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.main.MZmineModule;

public class ScatterPlotVisualizer implements MZmineModule, ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @see net.sf.mzmine.main.MZmineModule#initModule(net.sf.mzmine.main.MZmineCore)
	 */
	public void initModule() {

		MZmineCore.getDesktop().addMenuItem(MZmineMenu.VISUALIZATIONPEAKLIST,
				"Scatter plot",
				"Visualization of peak list data in scatter plot",
				KeyEvent.VK_S, false, this, null);

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		logger.finest("Opening scatter plot window");

		PeakList[] peakLists = MZmineCore.getDesktop().getSelectedPeakLists();

		for (PeakList peakList : peakLists)
			showNewScatterPlotWindow(peakList);

	}

	public static void showNewScatterPlotWindow(PeakList peakList) {

		if (peakList.getNumberOfRawDataFiles() < 2) {
			MZmineCore
					.getDesktop()
					.displayErrorMessage(
							"There is only one raw data file in the selected "
									+ "peak list, it is necessary at least two for comparison");
			return;
		}

		ScatterPlotWindow newWindow = new ScatterPlotWindow(peakList);

		MZmineCore.getDesktop().addInternalFrame(newWindow);

	}

	/**
	 * @see net.sf.mzmine.main.MZmineModule#toString()
	 */
	public String toString() {
		return "Scatter plot";
	}

	public ParameterSet getParameterSet() {
		return null;
	}

	public void setParameters(ParameterSet parameterValues) {
	}

}