package com.augurworks.decisiontree;

public interface NodeInfoContainer<K extends Copyable<K>,
		V extends Copyable<V>,
		T extends Copyable<T>> {
	public BinaryNode<K,V,T> getNode();
	public RowGroup<K,V,T> getRowGroup();
	public int getDepth();
}
