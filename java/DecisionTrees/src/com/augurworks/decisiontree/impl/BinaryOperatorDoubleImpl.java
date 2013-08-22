package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryOperator;

public enum BinaryOperatorDoubleImpl implements BinaryOperator<Double>{
	GT {
		@Override
		public boolean evaluate(Double leftHandSide, Double rightHandSide) {
			double l = leftHandSide;
			double r = rightHandSide;
			return l > r;
		}
	}, 
	LT {
		@Override
		public boolean evaluate(Double leftHandSide, Double rightHandSide) {
			double l = leftHandSide;
			double r = rightHandSide;
			return l < r;
		}
	},
	EQ {
		@Override
		public boolean evaluate(Double leftHandSide, Double rightHandSide) {
			double l = leftHandSide;
			double r = rightHandSide;
			return Math.abs(l - r) < 0.000001;
		}
	},
	;
	
	@Override
	public abstract boolean evaluate(Double leftHandSide, Double rightHandSide);

}
