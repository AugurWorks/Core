package com.augurworks.decisiontree.test;

import com.augurworks.decisiontree.CopyAble;

public enum WeatherData implements CopyAble<WeatherData> {
	OUTLOOK,
	TEMPERATURE,
	HUMIDITY,
	WINDY,
	HINT,
	;
	
	public static WeatherData fromString(String enumString) {
		for (WeatherData s : WeatherData.values()) {
			if (s.name().equalsIgnoreCase(enumString)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unkown WeatherData type: " + enumString);
	}

	@Override
	public WeatherData copy() {
		return this;
	}
}
