package com.augurworks.decisiontree.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.augurworks.decisiontree.CopyAble;
import com.augurworks.decisiontree.Row;

public class RowImpl<InputTypes extends CopyAble<InputTypes>, OutputTypes extends CopyAble<OutputTypes>, ChoiceTypes extends CopyAble<ChoiceTypes>> 
		implements Row<InputTypes, OutputTypes, ChoiceTypes> {
	private Map<InputTypes, OutputTypes> map = new HashMap<InputTypes, OutputTypes>();
	private ChoiceTypes result;

	@Override
	public void put(InputTypes key, OutputTypes value) {
		map.put(key, value);
	}

	@Override
	public OutputTypes get(InputTypes key) {
		return map.get(key);
	}

	@Override
	public boolean contains(InputTypes key) {
		return map.containsKey(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
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
		RowImpl<?, ?, ?> other = (RowImpl<?, ?, ?>) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RowStockImpl [map=" + map + "]";
	}

	@Override
	public Row<InputTypes, OutputTypes, ChoiceTypes> copy() {
		Row<InputTypes, OutputTypes, ChoiceTypes> copy = new RowImpl<InputTypes, OutputTypes, ChoiceTypes>();
		for (InputTypes key : map.keySet()) {
			copy.put(key.copy(), map.get(key).copy());
		}
		copy.setResult(result.copy());
		return copy;
	}

	@Override
	public ChoiceTypes getResult() {
		return result;
	}

	@Override
	public void setResult(ChoiceTypes result) {
		this.result = result;
	}

	@Override
	public Set<InputTypes> getColumnSet() {
		return map.keySet();
	}

}
