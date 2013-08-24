package com.augurworks.decisiontree;

import java.util.List;
import java.util.Set;


public interface RowGroup<K, V, T> extends CopyAble<RowGroup<K,V,T>> {
	public void addRow(Row<K, V, T> row);
	public int size();
	public Row<K, V, T> getRow(int index);
	public ColumnResult<K,V,T> getColumnResults(K input);
	public Set<K> getColumnSet();
	public List<T> getResults();
	public double getOriginalEntropy();
	public double getEntropy(TypeOperatorLimit<K,V> tol);
	public double getInformationGain(TypeOperatorLimit<K,V> tol);
	/** Removes the column with K type */
	public RowGroup<K,V,T> withoutType(K type);
	/** Removes the rows without values V on type K */
	public RowGroup<K,V,T> satisfying(TypeOperatorLimit<K,V> tol);
	public RowGroup<K,V,T> notSatisfying(TypeOperatorLimit<K,V> tol);
	public T getDomininantResult();
}
