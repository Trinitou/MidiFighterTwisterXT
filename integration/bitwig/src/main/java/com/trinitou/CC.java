package com.trinitou;

public enum CC {
    MIN(0x0), MAX(0x7F), RES(0x7F + 1), BITS(7);

    CC(int value) {
	this.value = value;
    }

    public final int value;
}
