package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.CopyAble;

public enum StockData implements CopyAble<StockData> {
	/** 
	 * (close price - open price) / close price  
	 */
	USO_PERCENT_CHANGE_YESTERDAY,
	USO_PERCENT_CHANGE_TWO_DAYS_AGO,
	/**
	 * (volume two days ago - volume yesterday) / volume yesterday
	 */
	USO_VOLUME_PERCENT_CHANGE_YESTERDAY,
	
	DJIA_PERCENT_CHANGE_YESTERDAY,
	DJIA_PERCENT_CHANGE_TWO_DAYS_AGO,
	DJIA_VOLUME_PERCENT_CHANGE_YESTERDAY,
	
	NASDAQ_PERCENT_CHANGE_YESTERDAY,
	NASDAQ_PERCENT_CHANGE_TWO_DAYS_AGO,
	NASDAQ_VOLUME_PERCENT_CHANGE_YESTERDAY,
	
	/** 
	 * 0 = sunday
	 * 1 = monday 
	 * ...
	 */
	DAY_OF_WEEK,
	DAY_OF_MONTH,
	/**
	 * 0 = january
	 * 1 = february
	 * ...
	 */
	MONTH_OF_YEAR,
	
	TICKER,
	PRICE,
	DATE,
	DAY_CHANGE,
	VOLUME,
	ADJUSTED_CLOSE,
	;
	
	public static StockData fromString(String enumString) {
		for (StockData s : StockData.values()) {
			if (s.name().equalsIgnoreCase(enumString)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unkown stockdata type: " + enumString);
	}

	@Override
	public StockData copy() {
		return this;
	}
}
