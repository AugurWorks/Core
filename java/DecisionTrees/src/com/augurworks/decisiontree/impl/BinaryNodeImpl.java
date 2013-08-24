package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.TypeOperatorLimit;

public class BinaryNodeImpl<InputType, OutputType, ReturnType> implements BinaryNode<InputType, OutputType, ReturnType> {
	private BinaryNode<InputType, OutputType, ReturnType> leftHandChild;
	private BinaryNode<InputType, OutputType, ReturnType> rightHandChild;
	private ReturnType defaultLeft;
	private ReturnType defaultRight;
	private TypeOperatorLimit<InputType, OutputType> typeOperatorLimit;
	
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultLeft == null) ? 0 : defaultLeft.hashCode());
		result = prime * result
				+ ((defaultRight == null) ? 0 : defaultRight.hashCode());
		result = prime * result
				+ ((leftHandChild == null) ? 0 : leftHandChild.hashCode());
		result = prime * result
				+ ((rightHandChild == null) ? 0 : rightHandChild.hashCode());
		result = prime
				* result
				+ ((typeOperatorLimit == null) ? 0 : typeOperatorLimit
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BinaryNodeImpl<?, ?, ?> other = (BinaryNodeImpl<?, ?, ?>) obj;
		if (defaultLeft == null) {
			if (other.defaultLeft != null)
				return false;
		} else if (!defaultLeft.equals(other.defaultLeft))
			return false;
		if (defaultRight == null) {
			if (other.defaultRight != null)
				return false;
		} else if (!defaultRight.equals(other.defaultRight))
			return false;
		if (leftHandChild == null) {
			if (other.leftHandChild != null)
				return false;
		} else if (!leftHandChild.equals(other.leftHandChild))
			return false;
		if (rightHandChild == null) {
			if (other.rightHandChild != null)
				return false;
		} else if (!rightHandChild.equals(other.rightHandChild))
			return false;
		if (typeOperatorLimit == null) {
			if (other.typeOperatorLimit != null)
				return false;
		} else if (!typeOperatorLimit.equals(other.typeOperatorLimit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BinaryNodeImpl [leftHandChild=" + leftHandChild
				+ ", rightHandChild=" + rightHandChild + ", defaultLeft="
				+ defaultLeft + ", defaultRight=" + defaultRight
				+ ", typeOperatorLimit=" + typeOperatorLimit + "]";
	}

	@Override
	public void setLeftHandChild(BinaryNode<InputType, OutputType, ReturnType> left) {
		leftHandChild = left;
	}

	@Override
	public void setDefaultLeft(ReturnType newDefLeft) {
		this.defaultLeft = newDefLeft;
	}

	@Override
	public void setDefaultRight(ReturnType newDefRight) {
		this.defaultRight = newDefRight;
	}

	@Override
	public void setTypeOperatorLimit(
			TypeOperatorLimit<InputType, OutputType> tol) {
		typeOperatorLimit = tol;
	}

}
