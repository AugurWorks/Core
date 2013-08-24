package com.augurworks.decisiontree;

public interface NodeInfoContainer<K extends CopyAble<K>,
		V extends CopyAble<V>,
		T extends CopyAble<T>> {
	public BinaryNode<K,V,T> getNode();
	public RowGroup<K,V,T> getRowGroup();
	public int getDepth();
}
