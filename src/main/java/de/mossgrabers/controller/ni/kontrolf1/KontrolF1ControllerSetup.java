// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1;

import java.util.List;

import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ColorManager;
import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ControlSurface;
import de.mossgrabers.controller.ni.kontrolf1.view.SessionView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.view.Views;


/**
 * Kontrol F1 extension (experiment/work in progress).
 *
 * @author Jürgen Moßgraber
 */
public class KontrolF1ControllerSetup extends AbstractControllerSetup<KontrolF1ControlSurface, KontrolF1Configuration>
{
    private static final int NUM_CONTROLLER_CHANNELS = 4;
    private static final int NUM_CONTROLLER_SCENES = 4;

    private static final List<ContinuousID> FADER_IDS        = ContinuousID.createSequentialList (ContinuousID.FADER1, NUM_CONTROLLER_CHANNELS);

    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public KontrolF1ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new KontrolF1ColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new KontrolF1Configuration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        // Set the session view highlight grid to correct size (4x4).
        ms.setNumTracks(NUM_CONTROLLER_CHANNELS);
        ms.setNumScenes(NUM_CONTROLLER_SCENES);
        // This shows a highlight rect on the selected clip (I think).
        // ms.setWantsClipLauncherNavigator(true);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, value) -> this.handleTrackChange (value));
        trackBank.setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
        "90????" /* Note on */);

        this.surfaces.add (new KontrolF1ControlSurface (this.host, this.colorManager, this.configuration, output, input));
    }

    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final KontrolF1ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, FADER_IDS));
        modeManager.setDefaultID (Modes.VOLUME);
        // Note the initial active mode is set in startup().
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final KontrolF1ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        // TBC - possibly indicates the type of midi event handled by `registerTriggerCommands`?
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // This is a shift mode, I might want one of these.
        // final BeatstepControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();

        // this.addButton (ButtonID.SHIFT, "Shift", (event, value) -> {

        //     if (event == ButtonEvent.DOWN)
        //     {
        //         viewManager.setActive (Views.SHIFT);
        //         return;
        //     }

        //     if (event == ButtonEvent.UP)
        //     {
        //         if (viewManager.isActive (Views.SHIFT))
        //             viewManager.restore ();

        //         // Red LED is turned off on button release, restore the correct color
        //         final BeatstepPadGrid beatstepPadGrid = (BeatstepPadGrid) surface.getPadGrid ();
        //         for (int note = 36; note < 52; note++)
        //         {
        //             final LightInfo lightInfo = beatstepPadGrid.getLightInfo (note);
        //             beatstepPadGrid.lightPad (note, lightInfo.getColor ());
        //         }
        //     }

        // }, BeatstepControlSurface.BEATSTEP_SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        // TBC - this is where I set up my channel remote controls.

        final KontrolF1ControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // for (int i = 0; i < 8; i++)
        // {
        //     this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowViewCommand (i, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_1 + i, RelativeEncoding.OFFSET_BINARY);
        //     this.addRelativeKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "Knob " + (i + 9), new KnobRowViewCommand (i + 8, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_9 + i, RelativeEncoding.OFFSET_BINARY);
        // }

        // this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Master", new PlayPositionCommand<> (this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, RelativeEncoding.OFFSET_BINARY);

        // final PlayView playView = (PlayView) viewManager.get (Views.PLAY);
        // playView.registerAftertouchCommand (new AftertouchViewCommand<> (playView, this.model, surface));

        // new TrackVolumeMode<> (surface, this.model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 4)).onActivate ();

        for (int i = 0; i < NUM_CONTROLLER_CHANNELS; i++) {
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, KontrolF1ControlSurface.KONTROLF1_MIDICHANNEL, KontrolF1ControlSurface.KONTROLF1_FADER_1 + i).setIndexInGroup(i);;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final KontrolF1ControlSurface surface = this.getSurface ();

        for (int channel=0; channel<NUM_CONTROLLER_CHANNELS; channel++) {
            surface.getContinuous (ContinuousID.get (ContinuousID.FADER1, channel)).setBounds (4 + (channel * 30), 30, 18, 60);
        }

        // FYI the pads are numbered from bottom left, columns then rows.
        final int left = 5;
        final int padJump = 30;
        surface.getButton (ButtonID.PAD1).setBounds (left + 0 * padJump, 100 + 3 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD2).setBounds (left + 1 * padJump, 100 + 3 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD3).setBounds (left + 2 * padJump, 100 + 3 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD4).setBounds (left + 3 * padJump, 100 + 3 * padJump, 20, 20);

        surface.getButton (ButtonID.PAD5).setBounds (left + 0 * padJump, 100 + 2 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD6).setBounds (left + 1 * padJump, 100 + 2 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD7).setBounds (left + 2 * padJump, 100 + 2 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD8).setBounds (left + 3 * padJump, 100 + 2 * padJump, 20, 20);

        surface.getButton (ButtonID.PAD9).setBounds  (left + 0 * padJump, 100 + 1 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD10).setBounds (left + 1 * padJump, 100 + 1 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD11).setBounds (left + 2 * padJump, 100 + 1 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD12).setBounds (left + 3 * padJump, 100 + 1 * padJump, 20, 20);

        surface.getButton (ButtonID.PAD13).setBounds (left + 0 * padJump, 100 + 0 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD14).setBounds (left + 1 * padJump, 100 + 0 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD15).setBounds (left + 2 * padJump, 100 + 0 * padJump, 20, 20);
        surface.getButton (ButtonID.PAD16).setBounds (left + 3 * padJump, 100 + 0 * padJump, 20, 20);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final KontrolF1ControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.SESSION);
        surface.getModeManager ().setActive (Modes.VOLUME);
    }


    protected void updateIndication ()
    {
        // TBC - Assuming not required, this is for specific lights/outputs; assuming the session view is handled elsewhere.
    }
}
