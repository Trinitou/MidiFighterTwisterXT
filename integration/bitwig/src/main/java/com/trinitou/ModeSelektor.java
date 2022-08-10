package com.trinitou;

import java.util.ArrayList;
import java.util.function.Function;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;

public class ModeSelektor {
    private final ArrayList<Mode> modes;
    private Mode currentMode;
    private final int numEncs;
    private Function<Integer, RelativeHardwareKnob> hardwareKnobsSupplier;

    public ModeSelektor(int numEncs, Function<Integer, RelativeHardwareKnob> hardwareKnobsSupplier) {
	this.modes = new ArrayList<Mode>();
	this.numEncs = numEncs;
	this.hardwareKnobsSupplier = hardwareKnobsSupplier;
    }

    public Mode createMode(String name) {
	Mode mode = new Mode(name, this.numEncs);
	modes.add(mode);
	return mode;
    }

    String[] getModeNames() {
	ArrayList<String> modeNames = new ArrayList<String>();
	for (Mode mode : this.modes) {
	    modeNames.add(mode.getName());
	}
	return modeNames.toArray(new String[0]);
    }

    private Mode findMode(String modeName) {
	for (Mode mode : this.modes) {
	    if (mode.getName().equals(modeName))
		return mode;
	}
	return null;
    }

    public void setCurrentMode(String modeName) {
	Mode pendingMode = this.findMode(modeName);
	if (this.currentMode != null) {
	    if (this.currentMode.equals(pendingMode))
		return;
	    this.currentMode.disconnect();
	}
	this.currentMode = pendingMode;
	this.currentMode.connect(this.hardwareKnobsSupplier);
    }

    public Mode currentMode() {
	return this.currentMode;
    }
}
