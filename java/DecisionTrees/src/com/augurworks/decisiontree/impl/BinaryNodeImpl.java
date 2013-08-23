package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.TypeOperatorLimit;

public class BinaryNodeImpl<InputType, OutputType, ReturnType> implements BinaryNode<InputType, OutputType, ReturnType> {
	private BinaryNode<InputType, OutputType, ReturnType> leftHandChild;
	private BinaryNode<InputType, OutputType, ReturnType> rightHandChild;
	private final ReturnType defaultLeft;
	private final ReturnType defaultRight;
	private final TypeOperatorLimit<InputType, OutputType> typeOperatorLimit;
	
	public BinaryNodeImpl(ReturnType defaultLeft, ReturnType defaultRight,
			TypeOperatorLimit<InputType, OutputType> typeOperatorLimit) {
		this.leftHandChild = null;
		this.rightHandChild = null;
		this.typeOperatorLimit = typeOperatorLimit;
		this.defaultLeft = defaultLeft;
		this.defaultRight = defaultRight;
	}

	@Override
	public BinaryNode<InputType, OutputType, ReturnType> getLeftHandChild() {
		return leftHandChild;
	}

	@Override
	public BinaryNode<InputType, OutputType, ReturnType> getRightHandChild() {
		return rightHandChild;
	}
	
	@Override
	public void setRightHandChild(
			BinaryNode<InputType, OutputType, ReturnType> right) {
		rightHandChild = right;
	}

	@Override
	public ReturnType evaluate(Row<InputType, OutputType, ReturnType> inputs) {
		boolean answer = typeOperatorLimit.getOperator()
				.evaluate(inputs.get(typeOperatorLimit.getType()), typeOperatorLimit.getLimit());
		if (answer) {
			return leftHandChild == null ? defaultLeft : leftHandChild.evaluate(inputs);
		} else {
			return rightHandChild == null ? defaultRight : rightHandChild.evaluate(inputs);
		}
	}

	@Override
	public BinaryOperator<OutputType> getOperator() {
		return typeOperatorLimit.getOperator();
	}	

	@Override
	public void setLeftHandChild(BinaryNode<InputType, OutputType, ReturnType> left) {
		leftHandChild = left;
	}

}
