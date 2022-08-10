package com.trinitou;

import java.util.function.Function;

import com.bitwig.extension.controller.api.RelativeHardwareKnob;

public class Mode {
    private final String name;
    private KnobFunction[] knobFunctions;
    private Runnable onConnectRunnable;
    private static final KnobFunction emptyKnobFunction = new EmptyKnobFunction();

    public Mode(String name, int numEncs) {
	this.name = name;
	this.knobFunctions = new KnobFunction[numEncs];
    }

    public String getName() {
	return this.name;
    }

    public void setKnobFunction(int index, KnobFunction knobFunction) {
	if (index < 0 || index >= this.knobFunctions.length)
	    return;
	this.knobFunctions[index] = knobFunction;
    }

    public KnobFunction getKnobFunction(int index) {
	KnobFunction knobFunction = this.knobFunctions[index];
	if (knobFunction == null)
	    return emptyKnobFunction;
	return knobFunction;
    }

    public void onConnect(Runnable runnable) {
	this.onConnectRunnable = runnable;
    }

    public void connect(Function<Integer, RelativeHardwareKnob> knobFunctionSupplier) {
	this.onConnectRunnable.run();
	for (int i = 0; i < this.knobFunctions.length; i++) {
	    getKnobFunction(i).addBindingsForKnob(knobFunctionSupplier.apply(i));
	}
    }

    public void disconnect() {
	for (int i = 0; i < this.knobFunctions.length; i++) {
	    getKnobFunction(i).removeBindings();
	}
    }
}
