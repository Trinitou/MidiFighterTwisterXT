package com.trinitou;

import java.util.Arrays;
import java.util.List;
import com.bitwig.extension.controller.api.MidiOut;
import com.trinitou.HostWrappers.CustomLogger;

public class HardwareOutput {
    private final static String SYSEX_MANUFACTURER_ID = "000179";
    private final static String SYSEX_COMMAND_NATIVE_MODE = intToSysExByte(5);
    private final static String SYSEX_HEADER = "F0" + SYSEX_MANUFACTURER_ID + SYSEX_COMMAND_NATIVE_MODE;
    private final static String SYSEX_END = "F7";

    private static enum NATIVE_MODE_COMMAND {
	SET_NATIVE_MODE_ACTIVE(0), ENC_CONFIG(1);

	public final byte value;

	NATIVE_MODE_COMMAND(int value) {
	    this.value = (byte) value;
	}
    }

    public enum ENC_CONFIG {
	INDICATOR(0), SWITCH(1);

	public final byte value;

	ENC_CONFIG(int value) {
	    this.value = (byte) value;
	}
    }

    public enum CC_CHANNEL {
	ENC_INDICATOR_POS(0);

	private int value;

	CC_CHANNEL(int value) {
	    this.value = ((byte) value) & 0xF;
	}
    }

    private final MidiOut midiOut;
    private final CustomLogger logger;

    public HardwareOutput(MidiOut midiOut, CustomLogger logger) {
	this.midiOut = midiOut;
	this.logger = logger;
    }

    private static String intToSysExByte(int integer) {
	if (integer < CC.MIN.value)
	    integer = CC.MIN.value;
	else if (integer > CC.MAX.value)
	    integer = CC.MAX.value;
	return String.format("%02X", integer);
    }

    private static String boolToSysExByte(boolean bool) {
	return intToSysExByte(bool ? 1 : 0);
    }

    public static byte boolToByte(boolean bool) {
	return (byte) (bool ? 1 : 0);
    }

    void sendNativeModeSysExCommand(final NATIVE_MODE_COMMAND type, final String commandSysExString) {
	final String sysExString = SYSEX_HEADER + intToSysExByte(type.value) + commandSysExString + SYSEX_END;
	logger.println("Send SysEx: " + sysExString);
	midiOut.sendSysex(sysExString);
    }

    public void sendNativeModeActive(boolean active) {
	logger.println("Native mode: " + (active ? "Enter" : "Leave"));
	sendNativeModeSysExCommand(NATIVE_MODE_COMMAND.SET_NATIVE_MODE_ACTIVE, boolToSysExByte(active));
    }

    private static String buildEncConfigSysExBlock(ENC_CONFIG config, int index, String sendState) {
	return intToSysExByte(config.value) + intToSysExByte(index) + sendState;
    }

    public void sendEncConfigSysEx(final ENC_CONFIG config, final int index, List<Byte> commandSysExBytes) {
	String commandSysExString = "";
	for (int i = 0; i < commandSysExBytes.size(); i++) {
	    commandSysExString += (intToSysExByte((int) (commandSysExBytes.get(i))));
	}
	sendNativeModeSysExCommand(NATIVE_MODE_COMMAND.ENC_CONFIG,
		buildEncConfigSysExBlock(config, index, commandSysExString));
    }

    public void sendCC(CC_CHANNEL channel, byte index, byte value) {
	midiOut.sendMidi(0xB0 | channel.value, index, value);
    }

    // takes RGB values and brightness as float
    void sendEncSwitchColor(int index, double r, double g, double b) {
	int red;
	int green;
	int blue;
	final boolean maxBrightnessColorMethod = false;
	// TODO: try to better match the actual track colors (via firmware or here)

	if (maxBrightnessColorMethod) {
	    final double sum = r + g + b;
	    final double invSum = (sum != 0) ? 3.0 / sum : 3.0;
	    red = (int) Math.floor(r * invSum * CC.MAX.value);
	    green = (int) Math.floor(g * invSum * CC.MAX.value);
	    blue = (int) Math.floor(b * invSum * CC.MAX.value);
	} else {
	    red = (int) (r * CC.MAX.value);
	    green = (int) (g * CC.MAX.value);
	    blue = (int) (b * CC.MAX.value);
	}
	Byte[] bytes = { (byte) red, (byte) green, (byte) blue };
	sendEncConfigSysEx(ENC_CONFIG.SWITCH, index, Arrays.asList(bytes));
    }
}
