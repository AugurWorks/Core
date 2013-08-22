package com.augurworks.decisiontree;

/**
 * Node of the decision tree.
 * @author saf
 *
 * @param <T> inputtype
 * @param <U> output type
 * @param <V> return type
 */
public interface BinaryNode<T,U,V> {
	public BinaryNode<T,U,V> getLeftHandChild();
	public BinaryNode<T,U,V> getRightHandChild();
	public void setLeftHandChild(BinaryNode<T,U,V> left);
	public void setRightHandChild(BinaryNode<T,U,V> right);
	public V evaluate(Row<T,U,V> inputs);
	public BinaryOperator<U> getOperator();
	public void setRightLimitor(U limit);
	public void setInputType(T input);
}
