package com.trinitou;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.trinitou.KnobIndicatorConfig.ENC_DISPLAY_TYPE;

public class EmptyKnobFunction implements KnobFunction {

    public static final KnobIndicatorConfig emptyEncIndicatorConfig = new KnobIndicatorConfig(ENC_DISPLAY_TYPE.BAR,
	    false, 0x50);
    private static final Color emptySwitchColor = Color.blackColor();

    @Override
    public void addBindingsForKnob(RelativeHardwareKnob knob) {
    }

    @Override
    public void removeBindings() {
    }

    @Override
    public double getIndicatorPos() {
	return 0.0;
    }

    @Override
    public KnobIndicatorConfig getIndicatorConfig() {
	return emptyEncIndicatorConfig;
    }

    @Override
    public Color getSwitchLedColor() {
	return emptySwitchColor;
    }
}
