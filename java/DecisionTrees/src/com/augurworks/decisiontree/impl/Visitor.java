package com.augurworks.decisiontree.impl;

public interface Visitor<T> {
    void visit(T t);
}
