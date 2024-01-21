// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1.controller;

import de.mossgrabers.controller.ni.kontrolf1.KontrolF1Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The Beatstep control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class KontrolF1ControlSurface extends AbstractControlSurface<KontrolF1Configuration>
{
    // Kontrol F1 is on channel 13 (aka 12 zero-based)
    public static final int KONTROLF1_MIDICHANNEL = 12;

    public static final int KONTROLF1_KNOB_1 = 2;
    public static final int KONTROLF1_KNOB_2 = 3;
    public static final int KONTROLF1_KNOB_3 = 4;
    public static final int KONTROLF1_KNOB_4 = 5;

    public static final int KONTROLF1_FADER_1 = 6;
    public static final int KONTROLF1_FADER_2 = 7;
    public static final int KONTROLF1_FADER_3 = 8;
    public static final int KONTROLF1_FADER_4 = 9;

    // private boolean         isShift;

    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public KontrolF1ControlSurface (final IHost host, final ColorManager colorManager, final KontrolF1Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new KontrolF1PadGrid (colorManager, output), 120, 294);
    }


}