package com.augurworks.decisiontree.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.BinaryOperatorSet;
import com.augurworks.decisiontree.Provider;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.impl.BinaryNodeImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.DecisionTrees;
import com.augurworks.decisiontree.impl.StockData;
import com.augurworks.decisiontree.impl.StockOrder;

public class StockDataTest {
//	private static String FILENAME = "/home/saf/code/aw_core/java/DecisionTrees/test/stockSample.csv";
	private static String FILENAME = "/home/saf/code/aw_core/infinite_scripts/answer.csv";
	
	private static RowGroup<StockData, CopyableDouble, StockOrder> rows = 
			DecisionTrees.parseData(FILENAME, new Provider<StockData>() {
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
		
		
	@Test
	public void workflowTest() {
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
		double correct = 0;
		double total = 0;
		for (int i = 0; i < rows.size(); i++) {
			Row<StockData, CopyableDouble, StockOrder> row = rows.getRow(i);
			if (row.getResult().equals(root.evaluate(row))) {
				correct++;
			}
			total++;
//			assertEquals(row.getResult(), root.evaluate(row));
		}
//		assertEquals(2, DecisionTrees.getDepth(root));
		System.out.println(root);
		System.out.println(correct / total * 100);
		System.out.println(DecisionTrees.getDepth(root));
	}

}
