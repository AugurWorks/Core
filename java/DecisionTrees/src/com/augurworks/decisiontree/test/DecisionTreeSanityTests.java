package com.augurworks.decisiontree.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.impl.BinaryNodeImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.RowImpl;
import com.augurworks.decisiontree.impl.StockData;
import com.augurworks.decisiontree.impl.StockOrder;

public class DecisionTreeSanityTests {
	
	@Test
	public void testRow() {
		Row<StockData, CopyableDouble, StockOrder> inputs = new RowImpl<StockData, CopyableDouble, StockOrder>();
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(11));
		assertEquals(new CopyableDouble(11), inputs.get(StockData.DAY_OF_MONTH));
		
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(12));
		assertEquals(new CopyableDouble(12), inputs.get(StockData.DAY_OF_MONTH));
		
		assertTrue(inputs.contains(StockData.DAY_OF_MONTH));
		assertFalse(inputs.contains(StockData.DAY_OF_WEEK));
		assertNull(inputs.get(StockData.DJIA_PERCENT_CHANGE_TWO_DAYS_AGO));
		
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(1));
		assertTrue(inputs.contains(StockData.DAY_OF_WEEK));
		assertEquals(new CopyableDouble(1), inputs.get(StockData.DAY_OF_WEEK));
	}
	
	@Test
	public void testOperator() {
		assertTrue(BinaryOperatorDoubleImpl.LT.evaluate(new CopyableDouble(1), new CopyableDouble(2)));
		assertFalse(BinaryOperatorDoubleImpl.LT.evaluate(new CopyableDouble(1), new CopyableDouble(0)));
		assertFalse(BinaryOperatorDoubleImpl.LT.evaluate(new CopyableDouble(1), new CopyableDouble(1)));
		
		assertTrue(BinaryOperatorDoubleImpl.GT.evaluate(new CopyableDouble(5), new CopyableDouble(2)));
		assertFalse(BinaryOperatorDoubleImpl.GT.evaluate(new CopyableDouble(1.3), new CopyableDouble(2)));
		assertFalse(BinaryOperatorDoubleImpl.GT.evaluate(new CopyableDouble(2), new CopyableDouble(2)));
		
		assertTrue(BinaryOperatorDoubleImpl.EQ.evaluate(new CopyableDouble(1), new CopyableDouble(1)));
		assertFalse(BinaryOperatorDoubleImpl.EQ.evaluate(new CopyableDouble(1), new CopyableDouble(2)));
		assertFalse(BinaryOperatorDoubleImpl.EQ.evaluate(new CopyableDouble(4), new CopyableDouble(2)));
	}
	
	@Test
	public void testNodes()	{
		BinaryNode<StockData, CopyableDouble, StockOrder> root = 
				new BinaryNodeImpl<StockData, CopyableDouble, StockOrder>(BinaryOperatorDoubleImpl.GT,
						StockOrder.BUY, StockOrder.HOLD);
		root.setInputType(StockData.DAY_OF_MONTH);
		root.setRightLimitor(new CopyableDouble(10));
		
		Row<StockData, CopyableDouble, StockOrder> inputs = new RowImpl<StockData, CopyableDouble, StockOrder>();
		
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(11));
		assertEquals(root.evaluate(inputs), StockOrder.BUY);
		
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(9));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
	}
	
	@Test
	public void testNodesWithChildren() {
		BinaryNode<StockData, CopyableDouble, StockOrder> root = 
				new BinaryNodeImpl<StockData, CopyableDouble, StockOrder>(BinaryOperatorDoubleImpl.GT,
						StockOrder.BUY, StockOrder.HOLD);
		root.setInputType(StockData.DAY_OF_MONTH);
		root.setRightLimitor(new CopyableDouble(10));
		
		BinaryNode<StockData, CopyableDouble, StockOrder> leftChild = 
				new BinaryNodeImpl<StockData, CopyableDouble, StockOrder>(BinaryOperatorDoubleImpl.LT,
						StockOrder.BUY, StockOrder.HOLD);
		leftChild.setInputType(StockData.DAY_OF_WEEK);
		leftChild.setRightLimitor(new CopyableDouble(3));
		
		root.setLeftHandChild(leftChild);
		
		Row<StockData, CopyableDouble, StockOrder> inputs = new RowImpl<StockData, CopyableDouble, StockOrder>();
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(11));
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(2));
		assertEquals(root.evaluate(inputs), StockOrder.BUY);
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(4));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
		
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(9));
		assertEquals(root.evaluate(inputs), StockOrder.HOLD);
	}
	
	@Test
	public void testCopy() {
		Row<StockData, CopyableDouble, StockOrder> inputs = new RowImpl<StockData, CopyableDouble, StockOrder>();
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(11));
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(2));
		assertNull(inputs.getResult());
		inputs.setResult(StockOrder.UNKNOWN);
		assertEquals(new CopyableDouble(11), inputs.get(StockData.DAY_OF_MONTH));
		assertEquals(new CopyableDouble(2), inputs.get(StockData.DAY_OF_WEEK));
		assertEquals(inputs.getResult(), StockOrder.UNKNOWN);
		
		Row<StockData, CopyableDouble, StockOrder> copy = inputs.copy();
		assertEquals(new CopyableDouble(11), copy.get(StockData.DAY_OF_MONTH));
		assertEquals(new CopyableDouble(2), copy.get(StockData.DAY_OF_WEEK));
		assertEquals(copy.getResult(), StockOrder.UNKNOWN);
		
		inputs.put(StockData.DAY_OF_MONTH, new CopyableDouble(9));
		inputs.put(StockData.DAY_OF_WEEK, new CopyableDouble(214));
		inputs.setResult(StockOrder.BUY);
		
		assertEquals(new CopyableDouble(9), inputs.get(StockData.DAY_OF_MONTH));
		assertEquals(new CopyableDouble(214), inputs.get(StockData.DAY_OF_WEEK));
		assertEquals(inputs.getResult(), StockOrder.BUY);
		
		assertEquals(new CopyableDouble(11), copy.get(StockData.DAY_OF_MONTH));
		assertEquals(new CopyableDouble(2), copy.get(StockData.DAY_OF_WEEK));
		assertEquals(copy.getResult(), StockOrder.UNKNOWN);
		
		
	}
}
