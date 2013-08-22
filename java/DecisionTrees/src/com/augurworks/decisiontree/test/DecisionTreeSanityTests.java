package com.augurworks.decisiontree.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.impl.BinaryNodeStockImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.RowStockImpl;
import com.augurworks.decisiontree.impl.StockData;
import com.augurworks.decisiontree.impl.StockOrder;

public class DecisionTreeSanityTests {
	
	@Test
	public void testRow() {
		Row<StockData, Double, StockOrder> inputs = new RowStockImpl();
		inputs.put(StockData.DAY_OF_MONTH, new Double(11));
		assertEquals(new Double(11), inputs.get(StockData.DAY_OF_MONTH));
		
		inputs.put(StockData.DAY_OF_MONTH, new Double(12));
		assertEquals(new Double(12), inputs.get(StockData.DAY_OF_MONTH));
		
		assertTrue(inputs.contains(StockData.DAY_OF_MONTH));
		assertFalse(inputs.contains(StockData.DAY_OF_WEEK));
		assertNull(inputs.get(StockData.DJIA_PERCENT_CHANGE_TWO_DAYS_AGO));
		
		inputs.put(StockData.DAY_OF_WEEK, new Double(1));
		assertTrue(inputs.contains(StockData.DAY_OF_WEEK));
		assertEquals(new Double(1), inputs.get(StockData.DAY_OF_WEEK));
	}
	
	@Test
	public void testOperator() {
		assertTrue(BinaryOperatorDoubleImpl.LT.evaluate(new Double(1), new Double(2)));
		assertFalse(BinaryOperatorDoubleImpl.LT.evaluate(new Double(1), new Double(0)));
		assertFalse(BinaryOperatorDoubleImpl.LT.evaluate(new Double(1), new Double(1)));
		
		assertTrue(BinaryOperatorDoubleImpl.GT.evaluate(new Double(5), new Double(2)));
		assertFalse(BinaryOperatorDoubleImpl.GT.evaluate(new Double(1.3), new Double(2)));
		assertFalse(BinaryOperatorDoubleImpl.GT.evaluate(new Double(2), new Double(2)));
		
		assertTrue(BinaryOperatorDoubleImpl.EQ.evaluate(new Double(1), new Double(1)));
		assertFalse(BinaryOperatorDoubleImpl.EQ.evaluate(new Double(1), new Double(2)));
		assertFalse(BinaryOperatorDoubleImpl.EQ.evaluate(new Double(4), new Double(2)));
	}
	
	@Test
	public void testNodes()	{
		BinaryNode<Double, StockData, StockOrder> root = new BinaryNodeStockImpl();
		root.setInputType(StockData.DAY_OF_MONTH);
		root.setOperator(BinaryOperatorDoubleImpl.GT);
		root.setRightLimitor(new Double(10));
		
		Row<StockData, Double, StockOrder> inputs = new RowStockImpl();
		
		inputs.put(StockData.DAY_OF_MONTH, new Double(11));
		assertEquals(root.evaluate(inputs), StockOrder.BUY);
		
		inputs.put(StockData.DAY_OF_MONTH, new Double(9));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
	}
	
	@Test
	public void testNodesWithChildren() {
		BinaryNode<Double, StockData, StockOrder> root = new BinaryNodeStockImpl();
		root.setInputType(StockData.DAY_OF_MONTH);
		root.setOperator(BinaryOperatorDoubleImpl.GT);
		root.setRightLimitor(new Double(10));
		
		BinaryNode<Double, StockData, StockOrder> leftChild = new BinaryNodeStockImpl();
		leftChild.setInputType(StockData.DAY_OF_WEEK);
		leftChild.setOperator(BinaryOperatorDoubleImpl.LT);
		leftChild.setRightLimitor(new Double(3));
		
		root.setLeftHandChild(leftChild);
		
		Row<StockData, Double, StockOrder> inputs = new RowStockImpl();
		inputs.put(StockData.DAY_OF_MONTH, new Double(11));
		inputs.put(StockData.DAY_OF_WEEK, new Double(2));
		assertEquals(root.evaluate(inputs), StockOrder.BUY);
		inputs.put(StockData.DAY_OF_WEEK, new Double(4));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
		
		inputs.put(StockData.DAY_OF_WEEK, new Double(9));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
	}
	
	@Test
	public void testCopy() {
		Row<StockData, Double, StockOrder> inputs = new RowStockImpl();
		inputs.put(StockData.DAY_OF_MONTH, new Double(11));
		inputs.put(StockData.DAY_OF_WEEK, new Double(2));
		assertNull(inputs.getResult());
		inputs.setResult(StockOrder.UNKNOWN);
		assertEquals(new Double(11), inputs.get(StockData.DAY_OF_MONTH));
		assertEquals(new Double(2), inputs.get(StockData.DAY_OF_WEEK));
		assertEquals(inputs.getResult(), StockOrder.UNKNOWN);
		
		Row<StockData, Double, StockOrder> copy = inputs.copy();
		assertEquals(new Double(11), copy.get(StockData.DAY_OF_MONTH));
		assertEquals(new Double(2), copy.get(StockData.DAY_OF_WEEK));
		assertEquals(copy.getResult(), StockOrder.UNKNOWN);
		
		inputs.put(StockData.DAY_OF_MONTH, new Double(9));
		inputs.put(StockData.DAY_OF_WEEK, new Double(214));
		inputs.setResult(StockOrder.BUY);
		
		assertEquals(new Double(9), inputs.get(StockData.DAY_OF_MONTH));
		assertEquals(new Double(214), inputs.get(StockData.DAY_OF_WEEK));
		assertEquals(inputs.getResult(), StockOrder.BUY);
		
		assertEquals(new Double(11), copy.get(StockData.DAY_OF_MONTH));
		assertEquals(new Double(2), copy.get(StockData.DAY_OF_WEEK));
		assertEquals(copy.getResult(), StockOrder.UNKNOWN);
		
		
	}
}
