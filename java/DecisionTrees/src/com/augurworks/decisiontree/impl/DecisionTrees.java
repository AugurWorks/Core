package com.augurworks.decisiontree.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.BinaryOperatorSet;
import com.augurworks.decisiontree.Copyable;
import com.augurworks.decisiontree.NodeInfoContainer;
import com.augurworks.decisiontree.Provider;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.TypeOperatorLimit;

public class DecisionTrees {
	private DecisionTrees() {}
	
	public static <K,V,T> TypeOperatorLimit<K,V> getBestInfoGainTol(
			RowGroup<K, V, T> rowSet, BinaryOperatorSet<V> binaryOps) {
		TypeOperatorLimit<K, V> bestTol = null;
		double bestInfoGain = -1;
		for (K column : rowSet.getColumnSet()) {
			for (V value : rowSet.getColumnResults(column).getValues()) {
				for (BinaryOperator<V> operator : binaryOps.operators()) {
					TypeOperatorLimit<K, V> tol = 
							new TypeOperatorLimitImpl<K, V>(
									column, value, operator);
					if (rowSet.getInformationGain(tol) > bestInfoGain) {
						bestInfoGain = rowSet.getInformationGain(tol);
						bestTol = tol;
					}
				}
			}
		}
		return bestTol;
	}
	
	public static <K extends Copyable<K>,V extends Copyable<V>,T extends Copyable<T>> 
		RowGroup<K,V,T> parseData(
			String filename, Provider<K> kProvider, Provider<V> vProvider, Provider<T> tProvider) {
		File f = new File(filename);
		if (!f.exists()) {
			throw new IllegalArgumentException("File " + filename
					+ " does not exist");
		}
		return readCsv(f, kProvider, vProvider, tProvider);
	}
	
	public static <K extends Copyable<K>,V extends Copyable<V>, T extends Copyable<T>> RowGroup<K, V, T> readCsv(File f,
			Provider<K> kProvider, Provider<V> vProvider, Provider<T> tProvider) {
		if (!f.exists()) {
			throw new IllegalArgumentException("File " + f + " does not exist");
		}
		if (!f.canRead()) {
			throw new IllegalArgumentException("File " + f + " cannot be read");
		}
		RowGroup<K, V, T> rows = 
				new RowGroupImpl<K, V, T>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));

			// Get the title line
			String line = br.readLine();
			if (line == null) {
				return rows;
			}
			String[] titles = line.split(",");
			List<K> titleNames = new ArrayList<K>();
			for (int i = 0; i < titles.length - 1; i++) {
				String title = titles[i].trim();
				K name = kProvider.fromString(title);
				if (titleNames.contains(name)) {
					throw new IllegalArgumentException(name
							+ " is duplicated in the input file");
				}
				titleNames.add(name);
			}
			
			// Data rows
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length != titleNames.size() + 1) {
					throw new IllegalArgumentException("Line " + line + " does not have the correct number of columns");
				}
				Row<K, V, T> row = 
						new RowImpl<K, V, T>();
				for (int i = 0; i < data.length - 1; i++) {
					K name = titleNames.get(i);
					row.put(name, vProvider.fromString(data[i].trim()));
				}
				// result should be last
				row.setResult(tProvider.fromString(data[data.length - 1].trim()));
				rows.addRow(row);
			}
			
			return rows;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rows;
	}
	
	public static <K extends Copyable<K>, V extends Copyable<V>, T extends Copyable<T>> BinaryNode<K,V,T> train(
			BinaryNode<K,V,T> root, RowGroup<K,V,T> rows, BinaryOperatorSet<V> opSet, int depthLimit) {
		Queue<NodeInfoContainer<K, V, T>> queue = 
				new LinkedList<NodeInfoContainer<K, V, T>>();
		queue.add(new NodeInfoContainerImpl<K, V, T>(root, rows, 0));
		while (queue.size() != 0) {
			NodeInfoContainer<K, V, T> nic = queue.remove();
			BinaryNode<K, V, T> node = nic.getNode();
			RowGroup<K, V, T> rowSet = nic.getRowGroup();
			TypeOperatorLimit<K, V> tol = DecisionTrees.getBestInfoGainTol(rowSet, opSet);
			node.setTypeOperatorLimit(tol);
			
			RowGroup<K, V, T> newRowSetLeft = rowSet.satisfying(tol);
			RowGroup<K, V, T> newRowSetRight = rowSet.notSatisfying(tol);
			int newDepth = nic.getDepth() + 1;
			
			if (newRowSetLeft.getOriginalEntropy() == 0 || nic.getDepth() == depthLimit) {
				// figure out what the left side should return
				node.setDefaultLeft(newRowSetLeft.getDomininantResult());
			} else {
				BinaryNode<K, V, T> newLeft = new BinaryNodeImpl<K, V, T>(
						null, null, null);
				node.setLeftHandChild(newLeft);
				queue.add(new NodeInfoContainerImpl<K, V, T>(newLeft, newRowSetLeft, newDepth));
			}
			
			if (newRowSetRight.getOriginalEntropy() == 0 || nic.getDepth() == depthLimit) {
				// figure out what the right side should return
				node.setDefaultRight(newRowSetRight.getDomininantResult());
			} else {
				BinaryNode<K, V, T> newRight = new BinaryNodeImpl<K, V, T>(
						null, null, null);
				node.setRightHandChild(newRight);
				queue.add(new NodeInfoContainerImpl<K, V, T>(newRight, newRowSetRight, newDepth));
			}
		}
		return root;
	}
	
	public static <K extends Copyable<K>, V extends Copyable<V>, T extends Copyable<T>> int getDepth(BinaryNode<K,V,T> node) {
		Queue<NodeInfoContainer<K,V,T>> queue = new LinkedList<NodeInfoContainer<K,V,T>>();
		queue.add(new NodeInfoContainerImpl<K, V, T>(node, null, 0));
		int maxDepth = -1;
		while (queue.size() != 0) {
			NodeInfoContainer<K,V,T> nic = queue.remove();
			if (nic.getNode() == null) {
				if (nic.getDepth() > maxDepth) {
					maxDepth = nic.getDepth();
				}
			} else {
				queue.add(new NodeInfoContainerImpl<K, V, T>(nic.getNode().getLeftHandChild(), null, 1+nic.getDepth()));
				queue.add(new NodeInfoContainerImpl<K, V, T>(nic.getNode().getRightHandChild(), null, 1+nic.getDepth()));
			}
		}
		return maxDepth;
	}
}