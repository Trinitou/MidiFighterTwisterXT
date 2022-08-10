package com.trinitou;

public class KnobIndicatorConfig {

    public final ENC_DISPLAY_TYPE displayType;
    public final boolean hasDetent;
    public final byte detentColor;

    public enum ENC_DISPLAY_TYPE {
	DOT(0), BAR(1), BLENDED_BAR(2), BLENDED_DOT(3);

	ENC_DISPLAY_TYPE(int value) {
	    this.value = (byte) value;
	}

	public final byte value;
    }

    public KnobIndicatorConfig(ENC_DISPLAY_TYPE displayType, boolean hasDetent, int detentColor) {
	this.displayType = displayType;
	this.hasDetent = hasDetent;
	this.detentColor = (byte) detentColor;
    }
}
