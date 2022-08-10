package com.trinitou;

import java.util.function.Function;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ColorValue;
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareActionBinding;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.RelativeHardwareControlToRangedValueBinding;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.bitwig.extension.controller.api.Track;
import com.trinitou.HostWrappers.CustomActionInitializer;
import com.trinitou.KnobIndicatorConfig.ENC_DISPLAY_TYPE;

public class BindableKnobFunction implements KnobFunction {
    private final Parameter parameter;
    private final ColorValue colorValue;
    private final HardwareActionBindable actionBindable;
    private final KnobIndicatorConfig indicatorConfig;

    private RelativeHardwareControlToRangedValueBinding parameterToKnobBinding;
    private HardwareActionBinding parameterResetToSwitchBinding;

    private BindableKnobFunction(Parameter parameter, HardwareActionBindable actionBindable, ColorValue colorValue,
	    KnobIndicatorConfig indicatorConfig) {
	this.parameter = parameter;
	this.parameter.value().markInterested();
	this.parameter.exists().markInterested();

	this.actionBindable = actionBindable;

	this.colorValue = colorValue;
	this.colorValue.markInterested();

	this.indicatorConfig = indicatorConfig;
    }

    @Override
    public void addBindingsForKnob(RelativeHardwareKnob knob) {
	this.parameterToKnobBinding = this.parameter.addBinding(knob);
	this.parameterResetToSwitchBinding = actionBindable.addBinding(knob.hardwareButton().pressedAction());
    }

    public void removeBindings() {
	assert (this.parameterToKnobBinding != null && parameterResetToSwitchBinding != null);
	this.parameterToKnobBinding.removeBinding();
	parameterResetToSwitchBinding.removeBinding();
    }

    @Override
    public double getIndicatorPos() {
	return this.parameter.value().get();
    }

    @Override
    public KnobIndicatorConfig getIndicatorConfig() {
	if (!this.parameter.exists().get())
	    return EmptyKnobFunction.emptyEncIndicatorConfig;
	return this.indicatorConfig;
    }

    @Override
    public Color getSwitchLedColor() {
	if (!this.parameter.exists().get())
	    return Color.blackColor();
	return this.colorValue.get();
    }

    private static class ResettableTrackParameterKnobFunction extends BindableKnobFunction {
	public ResettableTrackParameterKnobFunction(Track track, Function<Track, Parameter> parameterFromTrackFunction,
		CustomActionInitializer actionInitializer, KnobIndicatorConfig knobIndicatorConfig) {
	    super(parameterFromTrackFunction.apply(track), actionInitializer.createAction(() -> {
		parameterFromTrackFunction.apply(track).reset();
	    }, () -> {
		return "";
	    }), track.color(), knobIndicatorConfig);
	}
    }

    public static KnobFunction createTrackVolumeKnobFunction(Track track, CustomActionInitializer actionInitializer) {
	return (KnobFunction) new ResettableTrackParameterKnobFunction(track, (Track t) -> {
	    return t.volume();
	}, actionInitializer, new KnobIndicatorConfig(ENC_DISPLAY_TYPE.BAR, false, 0x0));
    }

    public static KnobFunction createTrackPanKnobFunction(Track track, CustomActionInitializer actionInitializer) {
	return (KnobFunction) new ResettableTrackParameterKnobFunction(track, (Track t) -> {
	    return t.pan();
	}, actionInitializer, new KnobIndicatorConfig(ENC_DISPLAY_TYPE.BAR, true, 0x7f));
    }
}
