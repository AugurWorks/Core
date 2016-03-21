package com.augurworks.decisiontree;

import java.io.Serializable;

/**
 * Node of the decision tree.
 * @author saf
 *
 * @param <T> inputtype
 * @param <U> output type
 * @param <V> return type
 */
public interface BinaryNode<T extends Copyable<T>, U extends Copyable<U>, V extends Copyable<V>> extends Serializable {
    BinaryNode<T,U,V> getLeftHandChild();
    BinaryNode<T,U,V> getRightHandChild();
    void setLeftHandChild(BinaryNode<T,U,V> left);
    void setRightHandChild(BinaryNode<T,U,V> right);
    void setTypeOperatorLimit(TypeOperatorLimit<T,U> tol);
    void setDefaultLeft(V newDefLeft);
    void setDefaultRight(V newDefRight);
    V evaluate(Row<T,U,V> inputs);
    BinaryOperator<U> getOperator();
    BinaryNode<T, U, V> copy();
    U getLimit();
    T getOperatorType();
    V getDefaultLeft();
    V getDefaultRight();
    String toFancyString();
    String toFancyString(int indents);
}
