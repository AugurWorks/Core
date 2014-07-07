package com.augurworks.decisiontree;

import java.io.Serializable;

public interface Copyable<T> extends Serializable {
	public T copy();
}
