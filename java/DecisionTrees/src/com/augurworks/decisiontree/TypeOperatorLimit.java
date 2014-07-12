package com.augurworks.decisiontree;

import java.io.Serializable;

public interface TypeOperatorLimit<K,V> extends Serializable {
	public K getType();
	public V getLimit();
	public BinaryOperator<V> getOperator();
	public TypeOperatorLimit<K, V> copy();
}
