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

package net.sf.mzmine.modules.peakpicking.deconvolution.noiseamplitude;

import java.text.NumberFormat;

import net.sf.mzmine.data.Parameter;
import net.sf.mzmine.data.ParameterType;
import net.sf.mzmine.data.impl.SimpleParameter;
import net.sf.mzmine.data.impl.SimpleParameterSet;
import net.sf.mzmine.main.MZmineCore;

public class NoiseAmplitudePeakDetectorParameters extends SimpleParameterSet {

	public static final NumberFormat percentFormat = NumberFormat
			.getPercentInstance();

	public static final Parameter minimumPeakHeight = new SimpleParameter(
			ParameterType.DOUBLE, "Min peak height",
			"Minimum acceptable peak height", "absolute", new Double(100.0),
			new Double(0.0), null, MZmineCore.getIntensityFormat());

	public static final Parameter minimumPeakDuration = new SimpleParameter(
			ParameterType.DOUBLE, "Min peak duration",
			"Minimum acceptable peak duration", null, new Double(10.0),
			new Double(0.0), null, MZmineCore.getRTFormat());

	public static final Parameter amplitudeOfNoise = new SimpleParameter(
			ParameterType.DOUBLE,
			"Amplitude of noise",
			"This vaue corresponds to the amplitude of noise present all the time in the signal",
			"absolute", new Double(1000.0), new Double(500.0), null, MZmineCore
					.getIntensityFormat());

	public NoiseAmplitudePeakDetectorParameters() {
		super(new Parameter[] { minimumPeakHeight, minimumPeakDuration,
				amplitudeOfNoise });
	}

}
