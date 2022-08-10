package com.trinitou;

import java.util.function.Supplier;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareActionBindable;

public class HostWrappers {
    public interface CustomActionInitializer {
	HardwareActionBindable createAction(Runnable runnable, Supplier<String> descriptionProvider);
    }

    private static class ActionInitializer implements CustomActionInitializer {
	private final ControllerHost host;

	public ActionInitializer(ControllerHost host) {
	    this.host = host;
	}

	@Override
	public HardwareActionBindable createAction(Runnable runnable, Supplier<String> descriptionProvider) {
	    return this.host.createAction(runnable, descriptionProvider);
	}
    }

    public static CustomActionInitializer createActionInitializer(ControllerHost host) {
	return new ActionInitializer(host);
    }

    public interface CustomLogger {
	public void println(String text);
    }

    private static class Logger implements CustomLogger {
	private final ControllerHost host;

	public Logger(ControllerHost host) {
	    this.host = host;
	}

	public void println(final String text) {
	    host.println(text);
	};
    }

    public static CustomLogger createLogger(ControllerHost host) {
	return new Logger(host);
    }
}
