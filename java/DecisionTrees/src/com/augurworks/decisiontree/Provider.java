package com.augurworks.decisiontree;

public interface Provider<T> {
	public T fromString(String s);
}
