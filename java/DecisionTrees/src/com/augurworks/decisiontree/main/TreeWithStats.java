package com.augurworks.decisiontree.main;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.DecisionTrees;
import com.augurworks.decisiontree.impl.StockData;
import com.augurworks.decisiontree.impl.StockOrder;

public class TreeWithStats {
	private final BinaryNode<StockData, CopyableDouble, StockOrder> root;
	private final RowGroup<StockData, CopyableDouble, StockOrder> rows;
	
	private TreeWithStats(BinaryNode<StockData, CopyableDouble, StockOrder> root,
			RowGroup<StockData, CopyableDouble, StockOrder> rows) {
		this.root = root;
		this.rows = rows.copy();
	}
	
	public static TreeWithStats of(BinaryNode<StockData, CopyableDouble, StockOrder> root,
			RowGroup<StockData, CopyableDouble, StockOrder> rows) {
		return new TreeWithStats(root, rows);
	}
	
	public int getTreeDepth() {
		return DecisionTrees.getDepth(root);
	}
	
	public double getCorrectPercent() {
		double correct = 0;
		double total = 0;
		for (int i = 0; i < rows.size(); i++) {
			Row<StockData, CopyableDouble, StockOrder> row = rows.getRow(i);
			if (row.getResult().equals(root.evaluate(row))) {
				correct++;
			}
			total++;
		}
		return 100.0 * correct / total;
	}

	@Override
	public String toString() {
		return "TreeWithStats [root=" + root + ", rows=" + rows
				+ ", getTreeDepth()=" + getTreeDepth()
				+ ", getCorrectPercent()=" + getCorrectPercent() + "]";
	}
}
