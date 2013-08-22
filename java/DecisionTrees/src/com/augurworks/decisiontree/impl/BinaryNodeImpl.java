package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.Row;

public class BinaryNodeImpl<InputType, OutputType, ReturnType> implements BinaryNode<InputType, OutputType, ReturnType> {
	private BinaryNode<InputType, OutputType, ReturnType> leftHandChild;
	private BinaryNode<InputType, OutputType, ReturnType> rightHandChild;
	private final ReturnType defaultLeft;
	private final ReturnType defaultRight;
	private BinaryOperator<OutputType> operator;
	private OutputType rightLimitor;
	private InputType leftType;
	
	public BinaryNodeImpl(BinaryOperator<OutputType> operator, ReturnType defaultLeft, ReturnType defaultRight) {
		this.leftHandChild = null;
		this.rightHandChild = null;
		this.operator = operator;
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
		boolean answer = operator.evaluate(inputs.get(leftType), rightLimitor);
		if (answer) {
			return leftHandChild == null ? defaultLeft : leftHandChild.evaluate(inputs);
		} else {
			return rightHandChild == null ? defaultRight : rightHandChild.evaluate(inputs);
		}
	}

	@Override
	public BinaryOperator<OutputType> getOperator() {
		return operator;
	}	

	@Override
	public void setLeftHandChild(BinaryNode<InputType, OutputType, ReturnType> left) {
		leftHandChild = left;
	}

	@Override
	public void setRightLimitor(OutputType limit) {
		rightLimitor = limit; 
	}

	@Override
	public void setInputType(InputType input) {
		leftType = input;
	}

}
