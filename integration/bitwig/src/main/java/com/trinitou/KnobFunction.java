package com.trinitou;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;

public interface KnobFunction {

    public void addBindingsForKnob(RelativeHardwareKnob knob);

    public void removeBindings();

    public double getIndicatorPos();

    public KnobIndicatorConfig getIndicatorConfig();

    public Color getSwitchLedColor();

}
