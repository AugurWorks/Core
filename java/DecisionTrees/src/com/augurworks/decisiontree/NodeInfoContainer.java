package com.augurworks.decisiontree;

public interface NodeInfoContainer<K,V,T> {
	public BinaryNode<K,V,T> getNode();
	public RowGroup<K,V,T> getRowGroup();
	public int getDepth();
}
