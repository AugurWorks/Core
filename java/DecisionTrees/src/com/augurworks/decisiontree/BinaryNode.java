package com.augurworks.decisiontree;

/**
 * Node of the decision tree.
 * @author saf
 *
 * @param <T> operator input type, also the row values
 * @param <U> type that defines row keys
 * @param <V> output of the evaluation
 */
public interface BinaryNode<T,U,V> {
	public BinaryNode<T,U,V> getLeftHandChild();
	public BinaryNode<T,U,V> getRightHandChild();
	public void setLeftHandChild(BinaryNode<T,U,V> left);
	public void setRightHandChild(BinaryNode<T,U,V> right);
	public V evaluate(Row<U,T,V> inputs);
	public BinaryOperator<T> getOperator();
	public void setOperator(BinaryOperator<T> operator);
	public void setRightLimitor(T limit);
	public void setInputType(U input);
}
