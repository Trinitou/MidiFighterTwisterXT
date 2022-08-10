package com.trinitou;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Bank;
import com.bitwig.extension.controller.api.ObjectProxy;
import com.bitwig.extension.controller.api.RelativeHardwareControlBinding;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.trinitou.KnobIndicatorConfig.ENC_DISPLAY_TYPE;

public class BankScrollerKnobFunction<ItemType extends ObjectProxy> implements KnobFunction {

    private final Bank<ItemType> bank;
    private final KnobIndicatorConfig indicatorConfig;
    RelativeHardwareControlBinding scrollPagesBinding;

    public BankScrollerKnobFunction(Bank<ItemType> bank) {
	this.bank = bank;
	this.bank.itemCount().markInterested();
	this.bank.scrollPosition().markInterested();
	this.indicatorConfig = new KnobIndicatorConfig(ENC_DISPLAY_TYPE.DOT, false, 0x0);
    }

    @Override
    public void addBindingsForKnob(RelativeHardwareKnob knob) {
	this.scrollPagesBinding = this.bank.addBinding(knob);
    }

    @Override
    public void removeBindings() {
	this.scrollPagesBinding.removeBinding();
    }

    @Override
    public double getIndicatorPos() {
	final int itemCount = this.bank.itemCount().get();
	final int currentBankCapacity = this.bank.getSizeOfBank();
	final double numberOfScrollPositions = (double) Math.max(itemCount - currentBankCapacity, 0);
	if (numberOfScrollPositions == 0.0)
	    return 0.0; // not able to scroll -> dot indicator not visible
	final double relativeScrollPosition = this.bank.scrollPosition().get() / numberOfScrollPositions;
	return ((relativeScrollPosition * 126) + 1) / 127.0; // scaled and shifted to range from 1 to 127
	// -> dot indicator visible should always be visible
	// TODO: add more display type(s) to firmware to represent banks/pages/stepped
	// parameters
    }

    @Override
    public KnobIndicatorConfig getIndicatorConfig() {
	return this.indicatorConfig;
    }

    @Override
    public Color getSwitchLedColor() {
	return Color.blackColor();
    }

}
