// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;


/**
 * Different colors to use for the pads and buttons of Beatstep.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class KontrolF1ColorManager extends ColorManager
{
    /** off. */
    public static final int    KONTROLF1_COLOR_OFF = 0;
    // ? it needs these colours when viewing simulated UI ?
    public static final int    KONTROLF1_COLOR_ON2 = 2;
    public static final int    KONTROLF1_COLOR_ON4 = 4;
    public static final int    KONTROLF1_COLOR_ON5 = 5;

    /**
     * Constructor.
     */
    public KontrolF1ColorManager ()
    {
        this.registerColorIndex (IPadGrid.GRID_OFF, KONTROLF1_COLOR_OFF);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, KONTROLF1_COLOR_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, KONTROLF1_COLOR_ON2);

        this.registerColor (KONTROLF1_COLOR_OFF, ColorEx.BLACK);

        // 2 == clip is playing.
        this.registerColor (KONTROLF1_COLOR_ON2, ColorEx.RED);
        // 4 == has clip in slot.
        this.registerColor (KONTROLF1_COLOR_ON4, ColorEx.GREEN);
        // 5 == no clip (aka stop button).
        this.registerColor (KONTROLF1_COLOR_ON5, ColorEx.BLACK);
    }
}