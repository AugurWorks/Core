package com.augurworks.decisiontree.impl;

import java.io.Serializable;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.Copyable;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;

public class TreeWithStats<T extends Copyable<T>,U extends Copyable<U>,V extends Copyable<V>> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final BinaryNode<T, U, V> root;
    private final RowGroup<T, U, V> rows;

    private TreeWithStats(BinaryNode<T, U, V> root,
            RowGroup<T, U, V> rows) {
        this.root = root;
        this.rows = rows.copy();
    }

    public static <T extends Copyable<T>,U extends Copyable<U>,V extends Copyable<V>> TreeWithStats<T, U, V> of(BinaryNode<T, U, V> root,
            RowGroup<T, U, V> rows) {
        return new TreeWithStats<T, U, V>(root, rows);
    }

    public int getTreeDepth() {
        return DecisionTrees.getDepth(root);
    }

    public double getCorrectPercent() {
        double correct = 0;
        double total = 0;
        for (int i = 0; i < rows.size(); i++) {
            Row<T, U, V> row = rows.getRow(i);
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

    public V evaluateRow(Row<T, U, V> inputRow) {
        return root.evaluate(inputRow);
    }

    public BinaryNode<T, U, V> getTree() {
        return root.copy();
    }

    public RowGroup<T, U, V> getRows() {
        return rows;
    }
}
