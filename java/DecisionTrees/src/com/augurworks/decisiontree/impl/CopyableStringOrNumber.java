package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.Copyable;

public class CopyableStringOrNumber implements Copyable<CopyableStringOrNumber> {
	private static final long serialVersionUID = 1L;
	private final String stringValue;
	private final double numericValue;
	private final boolean isNumber;
	
	private CopyableStringOrNumber(String stringValue, double numericValue, boolean isNumber) {
		this.isNumber = isNumber;
		this.numericValue = numericValue;
		this.stringValue = stringValue;
	}
	
	public CopyableStringOrNumber ofNumber(double value) {
		return new CopyableStringOrNumber(null	, value, true);
	}
	
	public CopyableStringOrNumber ofString(String value) {
		return new CopyableStringOrNumber(value, 0, false);
	}
	
	public CopyableStringOrNumber guessType(String value) {
		double val = 0;
		boolean isNum = true;
		try {
			val = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			isNum = false;
		}
		return new CopyableStringOrNumber(value, val, isNum);
	}
	
	public boolean isNumber() {
		return isNumber;
	}

	@Override
	public CopyableStringOrNumber copy() {
		return new CopyableStringOrNumber(stringValue, numericValue, isNumber);
	}

}
