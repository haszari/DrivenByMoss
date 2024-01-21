// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1.view;

import de.mossgrabers.controller.ni.kontrolf1.KontrolF1Configuration;
import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<KontrolF1ControlSurface, KontrolF1Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final KontrolF1ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 4, 4, false);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 6)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene s = sceneBank.getItem (scene);
        if (!s.doesExist ())
            return AbstractSessionView.COLOR_SCENE_OFF;

        if (s.isSelected ())
            return AbstractSessionView.COLOR_SCENE_OFF;

        return AbstractSessionView.COLOR_SCENE;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Birds-eye-view navigation
        // if (this.isBirdsEyeActive ())
        // {
        //     if (velocity == 0)
        //         return;

        //     final int index = note - 36;
        //     final int x = index % this.columns;
        //     final int y = this.rows - 1 - index / this.columns;

        //     this.onGridNoteBirdsEyeView (x, y, 0);
        //     return;
        // }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        super.onButton (buttonID, event, velocity);

        if (ButtonID.isSceneButton (buttonID) && event == ButtonEvent.UP && this.surface.isShiftPressed ())
            this.setAlternateInteractionUsed (true);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isSceneSelectAction ()
    {
        // Sadly, no additional button available
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        // if (super.handleButtonCombinations (track, slot))
        //     return true;

        // final int index = track.getIndex ();
        // if (index < 0)
        //     return true;

        // // Duplicate the slot with Select button
        // if (this.isButtonCombination (ButtonID.get (ButtonID.ROW1_1, index)))
        // {
        //     slot.duplicate ();
        //     return true;
        // }

        // // Delete the slot with Stop Clip button
        // if (this.isButtonCombination (ButtonID.get (ButtonID.ROW6_1, index)))
        // {
        //     slot.remove ();
        //     return true;
        // }

        return false;
    }
}