package com.augurworks.decisiontree.main;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.BinaryOperatorSet;
import com.augurworks.decisiontree.Provider;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.impl.BinaryNodeImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.DecisionTrees;
import com.augurworks.decisiontree.impl.StockData;
import com.augurworks.decisiontree.impl.StockOrder;
import com.augurworks.decisiontree.impl.TreeWithStats;

public class Main {
	public static RowGroup<StockData, CopyableDouble, StockOrder> parseFile(String filepath) {
		return DecisionTrees.parseData(filepath, new Provider<StockData>() {
			@Override
			public StockData fromString(String s) {
				return StockData.fromString(s);
			}
		}, new Provider<CopyableDouble>() {
			@Override
			public CopyableDouble fromString(String s) {
				return CopyableDouble.valueOf(s);
			}
		}, new Provider<StockOrder>() {
			@Override
			public StockOrder fromString(String s) {
				return StockOrder.fromString(s);
			}
		});
	}
	
	public static TreeWithStats runJob(RowGroup<StockData, CopyableDouble, StockOrder> rows) {
		BinaryNode<StockData, CopyableDouble, StockOrder> root = new BinaryNodeImpl<StockData, CopyableDouble, StockOrder>(
				StockOrder.BUY, StockOrder.HOLD, null);
		root = DecisionTrees.train(root, rows, new BinaryOperatorSet<CopyableDouble>() {
				@Override
				public Collection<BinaryOperator<CopyableDouble>> operators() {
					Set<BinaryOperator<CopyableDouble>> output = new HashSet<BinaryOperator<CopyableDouble>>();
					for (BinaryOperator<CopyableDouble> op : BinaryOperatorDoubleImpl.values()) {
						output.add(op);
					}
					return output;
				}				
			}, 0);
		return TreeWithStats.of(root, rows);
	}
	
	public static TreeWithStats runJob(String filepath) {
		RowGroup<StockData, CopyableDouble, StockOrder> rows = parseFile(filepath);
		return runJob(rows);
	}
}
