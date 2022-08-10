package com.trinitou;

import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class MidiFighterTwisterXTExtensionDefinition extends ControllerExtensionDefinition {
    private static final UUID DRIVER_ID = UUID.fromString("eb9dc3a4-d201-483f-afdc-e3ca70cc9f53");

    public MidiFighterTwisterXTExtensionDefinition() {
    }

    @Override
    public String getName() {
	return "Midi Fighter Twister XT";
    }

    @Override
    public String getAuthor() {
	return "Trinitou";
    }

    @Override
    public String getVersion() {
	return "0.1.0-alpha";
    }

    @Override
    public UUID getId() {
	return DRIVER_ID;
    }

    @Override
    public String getHardwareVendor() {
	return "DJ TechTools";
    }

    @Override
    public String getHardwareModel() {
	return "Midi Fighter Twister XT";
    }

    @Override
    public int getRequiredAPIVersion() {
	return 17;
    }

    @Override
    public int getNumMidiInPorts() {
	return 1;
    }

    @Override
    public int getNumMidiOutPorts() {
	return 1;
    }

    @Override
    public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list,
	    final PlatformType platformType) {
	if (platformType == PlatformType.WINDOWS) {
	    list.add(new String[] { "Midi Fighter Twister" }, new String[] { "Midi Fighter Twister" });
	} else if (platformType == PlatformType.MAC) {
	    // TODO: Set the correct names of the ports for auto detection on Windows
	    // platform here
	    // and uncomment this when port names are correct.
	    // list.add(new String[]{"Input Port 0"}, new String[]{"Output Port 0"});
	} else if (platformType == PlatformType.LINUX) {
	    // TODO: Set the correct names of the ports for auto detection on Windows
	    // platform here
	    // and uncomment this when port names are correct.
	    // list.add(new String[]{"Input Port 0"}, new String[]{"Output Port 0"});
	}
    }

    @Override
    public MidiFighterTwisterXTExtension createInstance(final ControllerHost host) {
	return new MidiFighterTwisterXTExtension(this, host);
    }
}
