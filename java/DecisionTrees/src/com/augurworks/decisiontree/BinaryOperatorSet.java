package com.augurworks.decisiontree;

import java.util.Collection;

public interface BinaryOperatorSet<V> {
	public Collection<BinaryOperator<V>> operators();
}
