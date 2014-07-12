package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.Copyable;

public enum StockOrder implements Copyable<StockOrder> {
	BUY,
	SELL,
	HOLD,
	UNKNOWN,
	;
	
	public static StockOrder fromString(String enumString) {
		for (StockOrder s : StockOrder.values()) {
			if (s.name().equalsIgnoreCase(enumString)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unkown stockorder type: " + enumString);
	}

	@Override
	public StockOrder copy() {
		return this;
	}
}
