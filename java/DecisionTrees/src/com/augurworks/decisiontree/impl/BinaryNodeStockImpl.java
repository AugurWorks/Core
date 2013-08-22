package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.Row;

public class BinaryNodeStockImpl implements BinaryNode<Double,StockData,StockOrder> {
	private BinaryNode<Double, StockData, StockOrder> leftHandChild;
	private BinaryNode<Double, StockData, StockOrder> rightHandChild;
	private StockOrder defaultLeft = StockOrder.BUY;
	private StockOrder defaultRight = StockOrder.HOLD;
	private BinaryOperator<Double> operator;
	private Double rightLimitor;
	private StockData leftType;
	
	public BinaryNodeStockImpl() {
		this.leftHandChild = null;
		this.rightHandChild = null;
		this.operator = null; 
	}

	@Override
	public BinaryNode<Double, StockData, StockOrder> getLeftHandChild() {
		return leftHandChild;
	}

	@Override
	public BinaryNode<Double, StockData, StockOrder> getRightHandChild() {
		return rightHandChild;
	}
	
	@Override
	public void setRightHandChild(
			BinaryNode<Double, StockData, StockOrder> right) {
		rightHandChild = right;
	}

	@Override
	public StockOrder evaluate(Row<StockData, Double, StockOrder> inputs) {
		boolean answer = operator.evaluate(inputs.get(leftType), rightLimitor);
		if (answer) {
			return leftHandChild == null ? defaultLeft : leftHandChild.evaluate(inputs);
		} else {
			return rightHandChild == null ? defaultRight : rightHandChild.evaluate(inputs);
		}
	}

	@Override
	public BinaryOperator<Double> getOperator() {
		return operator;
	}
	
	@Override
	public void setOperator(BinaryOperator<Double> op) {
		operator = op;
	}

	@Override
	public void setLeftHandChild(BinaryNode<Double, StockData, StockOrder> left) {
		leftHandChild = left;
	}

	@Override
	public void setRightLimitor(Double limit) {
		rightLimitor = limit; 
	}

	@Override
	public void setInputType(StockData input) {
		leftType = input;
	}

}
