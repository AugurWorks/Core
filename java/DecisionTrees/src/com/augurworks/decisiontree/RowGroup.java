package com.augurworks.decisiontree;

import java.util.List;
import java.util.Set;


public interface RowGroup<K, V, T> {
	public void addRow(Row<K, V, T> row);
	public int size();
	public Row<K, V, T> getRow(int index);
	public ColumnResult<K,V,T> getColumnResults(K input);
	public Set<K> getColumnSet();
	public List<T> getResults();
	public double getOriginalEntropy();
	public double getEntropy(K column, V value);
}
