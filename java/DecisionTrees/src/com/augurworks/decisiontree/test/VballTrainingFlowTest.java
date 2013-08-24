package com.augurworks.decisiontree.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.augurworks.decisiontree.TypeOperatorLimit;
import com.augurworks.decisiontree.impl.BinaryNodeImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.DecisionTrees;
import com.augurworks.decisiontree.impl.TypeOperatorLimitImpl;

public class VballTrainingFlowTest {
	private static String FILENAME = "/home/saf/augurworks_core/java/DecisionTrees/test/sample.csv";
	private static RowGroup<WeatherData, CopyableDouble, VBallPlay> rows = 
			DecisionTrees.parseData(FILENAME, new Provider<WeatherData>() {
					@Override
					public WeatherData fromString(String s) {
						return WeatherData.fromString(s);
					}
				}, new Provider<CopyableDouble>() {
					@Override
					public CopyableDouble fromString(String s) {
						return CopyableDouble.valueOf(s);
					}
				}, new Provider<VBallPlay>() {
					@Override
					public VBallPlay fromString(String s) {
						return VBallPlay.fromString(s);
					}
				});
	
	@Test
	public void testReadFile() {
		assertEquals(rows.size(), 14);
		Row<WeatherData, CopyableDouble, VBallPlay> row = rows.getRow(0);
		assertEquals(row.get(WeatherData.HUMIDITY), new CopyableDouble(5));
		assertEquals(row.getResult(), VBallPlay.NO);
	}
	
	@Test
	public void testEntropyColumn() {
		// original entropy
		assertTrue(Math.abs(rows.getOriginalEntropy() - 0.940) < 0.001);
		
		TypeOperatorLimit<WeatherData, CopyableDouble> tol = 
				new TypeOperatorLimitImpl<WeatherData, CopyableDouble>(WeatherData.HINT, 
						new CopyableDouble(0), BinaryOperatorDoubleImpl.EQ);
		assertTrue(rows.getInformationGain(tol) > 0);
	}
	
	@Test
	public void workflowTest() {
		BinaryNode<WeatherData, CopyableDouble, VBallPlay> root = new BinaryNodeImpl<WeatherData, CopyableDouble, VBallPlay>(
				VBallPlay.YES, VBallPlay.NO, null);
		root = DecisionTrees.train(root, rows, new BinaryOperatorSet<CopyableDouble>() {
			@Override
			public Collection<BinaryOperator<CopyableDouble>> operators() {
				Set<BinaryOperator<CopyableDouble>> output = new HashSet<BinaryOperator<CopyableDouble>>();
				for (BinaryOperator<CopyableDouble> op : BinaryOperatorDoubleImpl.values()) {
					output.add(op);
				}
				return output;
			}				
		}, 3);
		for (int i = 0; i < rows.size(); i++) {
			Row<WeatherData, CopyableDouble, VBallPlay> row = rows.getRow(i);
			assertEquals(row.getResult(), root.evaluate(row));
		}	
		System.out.println(root);
		assertEquals(1, DecisionTrees.getDepth(root));
	}
}
