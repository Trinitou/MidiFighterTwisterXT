package com.trinitou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.InternalHardwareLightState;
import com.bitwig.extension.controller.api.MultiStateHardwareLight;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.trinitou.CustomHardwareSurface.CCValueState;
import com.trinitou.CustomHardwareSurface.CustomHardwareProperty;
import com.trinitou.CustomHardwareSurface.SysExBytesState;
import com.trinitou.HardwareOutput.CC_CHANNEL;
import com.trinitou.HardwareOutput.ENC_CONFIG;
import com.trinitou.HostWrappers.CustomLogger;

public class MFTHardwareSurface {
    private static final int NUM_KNOB_ROWS = 4;
    private static final int NUM_KNOB_COLUMS = 4;
    private static final int NUM_KNOBS = NUM_KNOB_ROWS * NUM_KNOB_COLUMS;

    private final static String[] sideButtonNames = { "Top left", "Middle left", "Bottom left", "Top right",
	    "Middle right", "Bottom right", };
    private static final int NUM_SIDE_BUTTONS = sideButtonNames.length;

    public static int getKnobRowForIndex(int knobIndex) {
	return knobIndex / NUM_KNOB_COLUMS;
    }

    public static int getKnobRowForIndexFromBottom(int knobIndex) {
	return NUM_KNOB_ROWS - 1 - getKnobRowForIndex(knobIndex);
    }

    public static int getKnobColumnForIndex(int knobIndex) {
	return knobIndex % NUM_KNOB_COLUMS;
    }

    public static int getKnobColumnForIndexFromRight(int knobIndex) {
	return NUM_KNOB_COLUMS - 1 - getKnobColumnForIndex(knobIndex);
    }

    private enum HW_ELEMENT_ID {
	KNOB("enc"), BUTTON("switch"), BUTTON_LED("led"), SIDE_BUTTON("side_switch");

	private String prefix;

	HW_ELEMENT_ID(String prefix) {
	    this.prefix = prefix;
	}

	public String idForIndex(int index) {
	    return this.prefix + index;
	}
    }

    private final HardwareSurface hardwareSurface;
    private final CustomHardwareSurface extHardwareSurface;
    public final List<RelativeHardwareKnob> knobs;
    public final List<HardwareButton> sideButtons;

    class EncSwitchLightState extends InternalHardwareLightState {
	public EncSwitchLightState(Color color) {
	    this.color = color;
	}

	@Override
	public HardwareLightVisualState getVisualState() {
	    return null;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null)
		return false;
	    final Color otherColor = ((EncSwitchLightState) obj).color;
	    return this.color.getRed() == otherColor.getRed() && this.color.getGreen() == otherColor.getGreen()
		    && this.color.getBlue() == otherColor.getBlue();
	}

	final public Color color;
    }

    public MFTHardwareSurface(HardwareSurface hardwareSurface, HardwareInputInitializer hardwareInputInitializer,
	    Function<Integer, KnobFunction> indexedKnobFunctionSupplier, HardwareOutput hardwareOutput,
	    CustomLogger customLogger) {
	this.hardwareSurface = hardwareSurface;
	this.extHardwareSurface = new CustomHardwareSurface();

	hardwareSurface.setPhysicalSize(150, 150);

	this.knobs = new ArrayList<RelativeHardwareKnob>();
	this.sideButtons = new ArrayList<HardwareButton>();

	for (int i = 0; i < NUM_KNOBS; i++) {

	    // knob (input)
	    final RelativeHardwareKnob knob = hardwareSurface
		    .createRelativeHardwareKnob(HW_ELEMENT_ID.KNOB.idForIndex(i));
	    knob.setAdjustValueMatcher(hardwareInputInitializer.createKnobTwistedValueMatcher(i));
	    this.knobs.add(knob);

	    // button (input)
	    final HardwareButton button = hardwareSurface.createHardwareButton(HW_ELEMENT_ID.BUTTON.idForIndex(i));
	    knob.setHardwareButton(button);
	    button.pressedAction().setActionMatcher(hardwareInputInitializer.createKnobPressedActionMatcher(i));

	    // knob indicator configuration (output)
	    final int index = i;
	    CustomHardwareProperty<KnobIndicatorConfig, SysExBytesState> knobIndicatorConfig = this.extHardwareSurface
		    .createCustomHardwareProperty();
	    knobIndicatorConfig.setValueSupplier(() -> {
		return indexedKnobFunctionSupplier.apply(index).getIndicatorConfig();
	    });
	    knobIndicatorConfig.setInputValueToStateFunction((KnobIndicatorConfig config) -> {
		Byte[] bytes = { config.displayType.value, HardwareOutput.boolToByte(config.hasDetent),
			config.detentColor };
		return new SysExBytesState(Arrays.asList(bytes));
	    });
	    knobIndicatorConfig.onUpdateHardware((SysExBytesState state) -> {
		hardwareOutput.sendEncConfigSysEx(ENC_CONFIG.INDICATOR, index, state.value);
	    });

	    // knob indicator position (output)
	    CustomHardwareProperty<Double, CCValueState> knobIndicatorPos = this.extHardwareSurface
		    .createCustomHardwareProperty();
	    knobIndicatorPos.setValueSupplier(() -> {
		return indexedKnobFunctionSupplier.apply(index).getIndicatorPos();
	    });
	    knobIndicatorPos.setInputValueToStateFunction((Double value) -> {
		return new CCValueState((byte) Math.floor(value * CC.MAX.value));
	    });
	    knobIndicatorPos.onUpdateHardware((CCValueState state) -> {
		customLogger.println("Send MIDI CC " + index + ": " + state.value);
		hardwareOutput.sendCC(CC_CHANNEL.ENC_INDICATOR_POS, (byte) index, state.value);
	    });

	    // button led (output)
	    MultiStateHardwareLight buttonLed = hardwareSurface
		    .createMultiStateHardwareLight(HW_ELEMENT_ID.BUTTON_LED.idForIndex(i));
	    buttonLed.setColorToStateFunction((color) -> {
		return new EncSwitchLightState(color);
	    });
	    buttonLed.setColorSupplier(() -> {
		return indexedKnobFunctionSupplier.apply(index).getSwitchLedColor();
	    });
	    buttonLed.state().onUpdateHardware((encSwitchLightState) -> {
		assert !encSwitchLightState.equals(buttonLed.state().lastSentValue())
			: "Should have already been checked before!"; // TODO: remove

		Color color = ((EncSwitchLightState) encSwitchLightState).color;
		hardwareOutput.sendEncSwitchColor(index, color.getRed(), color.getGreen(), color.getBlue());
	    });
	}

	for (int i = 0; i < NUM_SIDE_BUTTONS; i++) {
	    // side button (input)
	    final HardwareButton sideButton = hardwareSurface
		    .createHardwareButton(HW_ELEMENT_ID.SIDE_BUTTON.idForIndex(i));
	    sideButton.setName(sideButtonNames[i]);
	    sideButton.pressedAction()
		    .setActionMatcher(hardwareInputInitializer.createSideButtonPressedActionMatcher(i));
	    this.sideButtons.add(sideButton);
	}
    }

    public void updateHardware() {
	extHardwareSurface.updateHardware();
	hardwareSurface.updateHardware();
    }
}
