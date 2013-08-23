package com.augurworks.decisiontree;

public interface TypeOperatorLimit<K,V> {
	public K getType();
	public V getLimit();
	public BinaryOperator<V> getOperator();
}
