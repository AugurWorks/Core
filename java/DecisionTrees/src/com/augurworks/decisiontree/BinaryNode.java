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
	public void setTypeOperatorLimit(TypeOperatorLimit<T,U> tol);
	public void setDefaultLeft(V newDefLeft);
	public void setDefaultRight(V newDefRight);
	public V evaluate(Row<T,U,V> inputs);
	public BinaryOperator<U> getOperator();
}
