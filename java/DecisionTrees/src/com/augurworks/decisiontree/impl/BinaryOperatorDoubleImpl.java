package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryOperator;

public enum BinaryOperatorDoubleImpl implements BinaryOperator<CopyableDouble>{
	GT(">") {
		@Override
		public boolean evaluate(CopyableDouble leftHandSide, CopyableDouble rightHandSide) {
			double l = leftHandSide.getValue();
			double r = rightHandSide.getValue();
			return l > r;
		}
	}, 
	LT("<") {
		@Override
		public boolean evaluate(CopyableDouble leftHandSide, CopyableDouble rightHandSide) {
			double l = leftHandSide.getValue();
			double r = rightHandSide.getValue();
			return l < r;
		}
	},
	EQ("=") {
		@Override
		public boolean evaluate(CopyableDouble leftHandSide, CopyableDouble rightHandSide) {
			double l = leftHandSide.getValue();
			double r = rightHandSide.getValue();
			return Math.abs(l - r) < 0.000001;
		}
	},
	;
	
	private String prettyPrint;
	private BinaryOperatorDoubleImpl(String pretty) {
		this.prettyPrint = pretty;
	}
	
	@Override
	public abstract boolean evaluate(CopyableDouble leftHandSide, CopyableDouble rightHandSide);

	@Override 
	public String toString() {
		return prettyPrint;
	}
}
