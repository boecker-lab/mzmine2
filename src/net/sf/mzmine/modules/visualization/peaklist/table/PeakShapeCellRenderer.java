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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.peaklist.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.OverlayLayout;
import javax.swing.table.TableCellRenderer;

import net.sf.mzmine.data.ChromatographicPeak;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.PeakListRow;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.modules.visualization.peaklist.PeakListTableParameters;
import net.sf.mzmine.util.components.CombinedXICComponent;
import net.sf.mzmine.util.components.PeakXICComponent;


/**
 * 
 */
class PeakShapeCellRenderer implements TableCellRenderer {

    private PeakList peakList;
    private PeakListTableParameters parameters;

    /**
     * 
     */
    PeakShapeCellRenderer(PeakList peakList, PeakListTableParameters parameters) {
        this.peakList = peakList;
        this.parameters = parameters;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JPanel newPanel = new JPanel();
        newPanel.setLayout(new OverlayLayout(newPanel));

        Color bgColor;

        if (isSelected)
            bgColor = table.getSelectionBackground();
        else
            bgColor = table.getBackground();

        newPanel.setBackground(bgColor);

        if (value instanceof ChromatographicPeak) {

            ChromatographicPeak peak = (ChromatographicPeak) value;
            double maxHeight = 0;

            switch (parameters.getPeakShapeNormalization()) {
            case GLOBALMAX:
                maxHeight = peakList.getDataPointMaxIntensity();
                break;
            case ROWMAX:
                int rowNumber = peakList.getPeakRowNum(peak);
                maxHeight = peakList.getRow(rowNumber).getDataPointMaxIntensity();
                break;
            default:
                maxHeight = peak.getRawDataPointsIntensityRange().getMax();
                break;
            }

            PeakXICComponent xic = new PeakXICComponent(peak, maxHeight);

            newPanel.add(xic);

            newPanel.setToolTipText(xic.getToolTipText());

        }

        if (value instanceof PeakListRow) {

            PeakListRow plRow = (PeakListRow) value;

            RawDataFile[] dataFiles = peakList.getRawDataFiles();
            ChromatographicPeak[] peaks = new ChromatographicPeak[dataFiles.length];
            for (int i = 0; i < dataFiles.length; i++) {
                peaks[i] = plRow.getPeak(dataFiles[i]);
            }

            CombinedXICComponent xic = new CombinedXICComponent(peaks,
                    plRow.getID());

            newPanel.add(xic);

            newPanel.setToolTipText(xic.getToolTipText());

        }

        return newPanel;

    }

}
