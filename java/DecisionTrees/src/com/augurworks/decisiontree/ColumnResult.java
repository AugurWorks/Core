package com.augurworks.decisiontree;

import java.util.List;
import java.util.Set;

public interface ColumnResult<K, V, T> {
	public List<V> getValues();
	public Set<V> getValueSet();
	public List<T> getResults();
	public Set<T> getResultSet();
	public void putResult(V value, T result);
	public int size(); 
	public K getInputType();
	public V getValue(int index);
	public T getResult(int index);
}
