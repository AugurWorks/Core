package com.augurworks.decisiontree;

public interface BinaryOperator<T> {
	public boolean evaluate(T leftHandSide, T rightHandSide);
}
