package com.trinitou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.trinitou.HostWrappers.CustomActionInitializer;
import com.trinitou.HostWrappers.CustomLogger;
import com.bitwig.extension.controller.ControllerExtension;

public class MidiFighterTwisterXTExtension extends ControllerExtension {
    private HardwareOutput hardwareOutput;

    private MFTHardwareSurface mftHardwareSurface;

    private ModeSelektor modeSelektor;

    private TrackBank trackBank;
    private MasterTrack masterTrack;

    protected MidiFighterTwisterXTExtension(final MidiFighterTwisterXTExtensionDefinition definition,
	    final ControllerHost host) {
	super(definition, host);
    }

    @Override
    public void init() {
	final ControllerHost host = getHost();
	final CustomLogger customLogger = HostWrappers.createLogger(host);

	hardwareOutput = new HardwareOutput(host.getMidiOutPort(0), customLogger);
	hardwareOutput.sendNativeModeActive(true);

	mftHardwareSurface = new MFTHardwareSurface(host.createHardwareSurface(),
		new HardwareInputInitializer(host.getMidiInPort(0)), (Integer index) -> {
		    return modeSelektor.currentMode().getKnobFunction(index);
		}, hardwareOutput, customLogger);

	modeSelektor = new ModeSelektor(mftHardwareSurface.knobs.size(), (Integer index) -> {
	    return mftHardwareSurface.knobs.get(index);
	});
	Mode trackVolumeMode = modeSelektor.createMode("Volume");
	Mode trackPanMode = modeSelektor.createMode("Pan");

	final String[] modeNames = modeSelektor.getModeNames();
	final SettableEnumValue currentModeSetting = host.getDocumentState().getEnumSetting("Current mode", "General",
		modeNames, modeNames[0]); // select the first mode per default
	currentModeSetting.addValueObserver((String modeName) -> {
	    modeSelektor.setCurrentMode(modeName);
	});
	final String emptySideButtonActionName = "-";
	final List<String> sideButtonActions = new ArrayList<String>();
	sideButtonActions.add(emptySideButtonActionName);
	sideButtonActions.addAll(Arrays.asList(modeNames));

	trackBank = host.createTrackBank(mftHardwareSurface.knobs.size(), 0, 0, false); // maximum of 16 tracks
	masterTrack = host.createMasterTrack(0);

	final CustomActionInitializer customActionInitializer = HostWrappers.createActionInitializer(host);

	for (int knobIndex = 0; knobIndex < mftHardwareSurface.knobs.size(); knobIndex++) {
	    final int row = MFTHardwareSurface.getKnobRowForIndex(knobIndex);
	    final int column = MFTHardwareSurface.getKnobColumnForIndex(knobIndex);
	    final int rowFromBottom = MFTHardwareSurface.getKnobRowForIndexFromBottom(knobIndex);
	    final int columnFromRight = MFTHardwareSurface.getKnobColumnForIndexFromRight(knobIndex);

	    final int trackBankEncoderRows = 3;
	    trackVolumeMode.onConnect(() -> {
		this.trackBank.setSizeOfBank(mftHardwareSurface.knobs.size());
	    });
	    trackPanMode.onConnect(() -> {
		this.trackBank.setSizeOfBank(mftHardwareSurface.knobs.size());
	    });
	    if (row < trackBankEncoderRows) {
		final Track track = this.trackBank.getItemAt(knobIndex);
		trackVolumeMode.setKnobFunction(knobIndex,
			BindableKnobFunction.createTrackVolumeKnobFunction(track, customActionInitializer));
		trackPanMode.setKnobFunction(knobIndex,
			BindableKnobFunction.createTrackPanKnobFunction(track, customActionInitializer));
	    } else if (rowFromBottom == 0) {
		if (column == 0) {
		    trackVolumeMode.setKnobFunction(knobIndex, new BankScrollerKnobFunction<Track>(this.trackBank));
		    trackPanMode.setKnobFunction(knobIndex, new BankScrollerKnobFunction<Track>(this.trackBank));
		} else if (columnFromRight == 0) {
		    trackVolumeMode.setKnobFunction(knobIndex, BindableKnobFunction
			    .createTrackVolumeKnobFunction(this.masterTrack, customActionInitializer));
		    trackPanMode.setKnobFunction(knobIndex,
			    BindableKnobFunction.createTrackPanKnobFunction(this.masterTrack, customActionInitializer));
		}

	    }
	}

	int sideButtonActionIndex = 0;
	for (final HardwareButton sideButton : mftHardwareSurface.sideButtons) {
	    String defaultActionName;
	    if (sideButtonActionIndex < modeNames.length)
		defaultActionName = modeNames[sideButtonActionIndex]; // assign available modes to side buttons
	    else
		defaultActionName = emptySideButtonActionName;
	    final String[] sideButtonActionNames = sideButtonActions.toArray(new String[0]);
	    final SettableEnumValue setting = host.getPreferences().getEnumSetting(sideButton.getName(),
		    "Side button mode assignments", sideButtonActionNames, defaultActionName);
	    setting.markInterested();

	    HardwareActionBindable action = customActionInitializer.createAction(() -> {
		String sideButtonActionName = setting.get();
		if (sideButtonActionName.equals(emptySideButtonActionName))
		    return;
		currentModeSetting.set(sideButtonActionName);
	    }, () -> String.format("Set current mode: %s", setting.get()));
	    action.addBinding(sideButton.pressedAction());
	    sideButtonActionIndex++;
	}

	host.showPopupNotification("Midi Fighter Twister XT Initialized");
    }

    @Override
    public void exit() {
	hardwareOutput.sendNativeModeActive(false);
	getHost().showPopupNotification("Midi Fighter Twister XT Exited");
    }

    @Override
    public void flush() {
	mftHardwareSurface.updateHardware();
    }
}
