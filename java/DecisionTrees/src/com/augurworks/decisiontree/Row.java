package com.augurworks.decisiontree;

import java.util.Set;

/**
 * Row is a glorified map.
 * @author saf
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public interface Row<K, V, T> extends CopyAble<Row<K,V,T>> {
	public void put(K key, V value);
	public V get(K key);
	public boolean contains(K key);
	public Row<K,V,T> copy();
	public T getResult();
	public void setResult(T result);
	public Set<K> getColumnSet();
	public Row<K,V,T> withoutKey(K type);
	public boolean matches(TypeOperatorLimit<K,V> tol);
}
