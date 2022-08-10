package com.trinitou;

import com.bitwig.extension.controller.api.HardwareActionMatcher;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.RelativeHardwareValueMatcher;

public class HardwareInputInitializer {
    private MidiIn midiIn;

    public HardwareInputInitializer(MidiIn midiIn) {
	this.midiIn = midiIn;
    }

    enum CHANNEL {
	ENC_ROTARY(0), ENC_SWITCH(1), SIDE_SWITCH(2);

	CHANNEL(int value) {
	    this.value = value;
	}

	public final int value;
    }

    public RelativeHardwareValueMatcher createKnobTwistedValueMatcher(int index) {
	return this.midiIn.createRelative2sComplementValueMatcher(
		"status == 0xB" + CHANNEL.ENC_ROTARY.value + " && data1 == " + index, "data2 - 64", CC.BITS.value,
		CC.MAX.value);
    }

    public HardwareActionMatcher createKnobPressedActionMatcher(int index) {
	return this.midiIn.createCCActionMatcher(CHANNEL.ENC_SWITCH.value, index, CC.MAX.value);
    }

    public HardwareActionMatcher createSideButtonPressedActionMatcher(int index) {
	return this.midiIn.createCCActionMatcher(CHANNEL.SIDE_SWITCH.value, index, CC.MAX.value);
    }
}
