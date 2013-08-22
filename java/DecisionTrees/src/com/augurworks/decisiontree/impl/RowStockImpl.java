package com.augurworks.decisiontree.impl;

import java.util.HashMap;
import java.util.Map;

import com.augurworks.decisiontree.Row;

public class RowStockImpl implements Row<StockData, Double, StockOrder> {
	private Map<StockData, Double> map = new HashMap<StockData, Double>();
	private StockOrder result;

	@Override
	public void put(StockData key, Double value) {
		map.put(key, value);
	}

	@Override
	public Double get(StockData key) {
		return map.get(key);
	}

	@Override
	public boolean contains(StockData key) {
		return map.containsKey(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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
		RowStockImpl other = (RowStockImpl) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RowStockImpl [map=" + map + "]";
	}

	@Override
	public Row<StockData, Double, StockOrder> copy() {
		Row<StockData, Double, StockOrder> copy = new RowStockImpl();
		for (StockData key : map.keySet()) {
			copy.put(key, map.get(key).doubleValue());
		}
		copy.setResult(result);
		return copy;
	}

	@Override
	public StockOrder getResult() {
		return result;
	}

	@Override
	public void setResult(StockOrder result) {
		this.result = result;
	}

}
