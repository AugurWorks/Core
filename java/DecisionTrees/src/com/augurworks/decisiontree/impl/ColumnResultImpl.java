package com.augurworks.decisiontree.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.augurworks.decisiontree.ColumnResult;

public class ColumnResultImpl<K,V,T> implements ColumnResult<K, V, T> {
	private List<V> values = new ArrayList<V>();
	private List<T> results = new ArrayList<T>();
	private final K inputType;

	public ColumnResultImpl(K inputType) {
		this.inputType = inputType;
	}
	
	@Override
	public List<V> getValues() {
		return values;
	}

	@Override
	public List<T> getResults() {
		return results;
	}

	@Override
	public K getInputType() {
		return inputType;
	}

	@Override
	public String toString() {
		return "ColumnResultImpl [values=" + values + ", results=" + results
				+ ", inputType=" + inputType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inputType == null) ? 0 : inputType.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		ColumnResultImpl<?, ?, ?> other = (ColumnResultImpl<?, ?, ?>) obj;
		if (inputType == null) {
			if (other.inputType != null)
				return false;
		} else if (!inputType.equals(other.inputType))
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public void putResult(V value, T result) {
		values.add(value);
		results.add(result);
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public Set<V> getValueSet() {
		Set<V> valueSet = new HashSet<V>();
		for (V val : values) {
			valueSet.add(val);
		}
		return valueSet;
	}

	@Override
	public Set<T> getResultSet() {
		Set<T> resultSet = new HashSet<T>();
		for (T res : results) {
			resultSet.add(res);
		}
		return resultSet;
	}

	@Override
	public V getValue(int index) {
		return values.get(index);
	}

	@Override
	public T getResult(int index) {
		return results.get(index);
	}
}
