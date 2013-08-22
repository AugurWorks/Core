package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.CopyAble;

public class CopyableDouble implements CopyAble<CopyableDouble> {
	private double value;
	
	public CopyableDouble(double value) {
		this.value = value;
	}

	@Override
	public CopyableDouble copy() {
		return new CopyableDouble(value);
	}
	
	public double getValue() {
		return value;
	}
	
	public static CopyableDouble valueOf(double d) {
		return new CopyableDouble(d);
	}
	
	@Override
	public String toString() {
		return "CopyableDouble [value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CopyableDouble other = (CopyableDouble) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	public static CopyableDouble valueOf(Double d) {
		return new CopyableDouble(d.doubleValue());
	}
	
	public static CopyableDouble valueOf(String d) {
		return new CopyableDouble(Double.parseDouble(d));
	}

}
