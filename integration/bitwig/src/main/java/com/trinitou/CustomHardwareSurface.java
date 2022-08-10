package com.trinitou;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomHardwareSurface {
    private ArrayList<UpdatableHardwareOutputElement> outputElements;

    public CustomHardwareSurface() {
	this.outputElements = new ArrayList<UpdatableHardwareOutputElement>();
    }

    private static abstract class CustomHardwareOutputState<ValueType> {
	public final ValueType value;

	protected CustomHardwareOutputState(ValueType value) {
	    this.value = value;
	}

	public abstract boolean isEqualTo(CustomHardwareOutputState<ValueType> state);
    }

    public static final class SysExBytesState extends CustomHardwareOutputState<List<Byte>> {

	public SysExBytesState(List<Byte> value) {
	    super(value);
	}

	@Override
	public boolean isEqualTo(CustomHardwareOutputState<List<Byte>> state) {
	    if (this.value.size() != state.value.size())
		throw new IllegalArgumentException("Should always be of the same fixed size!");

	    for (int i = 0; i < this.value.size(); i++) {
		if (this.value.get(i) != state.value.get(i))
		    return false;
	    }
	    return true;
	}
    }

    private interface UpdatableHardwareOutputElement {
	public void update();
    }

    public interface CustomHardwareProperty<InputValueType, StateType extends CustomHardwareOutputState<?>> {
	public void setValueSupplier(Supplier<InputValueType> valueSupplier);

	public void setInputValueToStateFunction(Function<InputValueType, StateType> inputToStateFunction);

	public void onUpdateHardware(Consumer<StateType> sendValueConsumer);
    }

    private class CustomHardwareOutputElement<InputValueType, StateType extends CustomHardwareOutputState<?>>
	    implements UpdatableHardwareOutputElement, CustomHardwareProperty<InputValueType, StateType> {
	private StateType currentState;
	private Function<InputValueType, StateType> inputToStateFunction;
	private Supplier<InputValueType> valueSupplier;
	private Consumer<StateType> sendValueConsumer;

	protected CustomHardwareOutputElement() {
	    this.currentState = null;
	}

	@Override
	public void setValueSupplier(Supplier<InputValueType> valueSupplier) {
	    this.valueSupplier = valueSupplier;
	}

	public void setInputValueToStateFunction(Function<InputValueType, StateType> inputToStateFunction) {
	    this.inputToStateFunction = inputToStateFunction;
	}

	@Override
	public void onUpdateHardware(Consumer<StateType> sendValueConsumer) {
	    this.sendValueConsumer = sendValueConsumer;
	}

	@Override
	public void update() {
	    final StateType pendingState = this.inputToStateFunction.apply(this.valueSupplier.get());
	    if (currentState != null && pendingState.value.equals(currentState.value))
		return;
	    currentState = pendingState;
	    this.sendValueConsumer.accept(pendingState);
	}
    }

    public <A, B extends CustomHardwareOutputState<?>> CustomHardwareProperty<A, B> createCustomHardwareProperty() {
	final CustomHardwareOutputElement<A, B> customhardwareProperty = new CustomHardwareOutputElement<A, B>();
	this.outputElements.add(customhardwareProperty);
	return customhardwareProperty;
    }

    public static final class CCValueState extends CustomHardwareOutputState<Byte> {

	public CCValueState(byte value) {
	    super(value);
	}

	@Override
	public boolean isEqualTo(CustomHardwareOutputState<Byte> state) {
	    return this.value.equals(state.value);
	}
    }

    public void updateHardware() {
	for (UpdatableHardwareOutputElement outputElement : this.outputElements) {
	    outputElement.update();
	}
    }
}
