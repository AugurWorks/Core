package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.CopyAble;
import com.augurworks.decisiontree.NodeInfoContainer;
import com.augurworks.decisiontree.RowGroup;

public class NodeInfoContainerImpl<K extends CopyAble<K>,
		V extends CopyAble<V>,
		T extends CopyAble<T>> implements NodeInfoContainer<K,V,T> {
	private final BinaryNode<K,V,T> node;
	private final RowGroup<K,V,T> rowGroup;
	private final int depth;
	
	public NodeInfoContainerImpl(BinaryNode<K,V,T> node, RowGroup<K,V,T> rowGroup, int depth) {
		this.node = node;
		this.rowGroup = rowGroup;
		this.depth = depth;
	}

	@Override
	public BinaryNode<K, V, T> getNode() {
		return node;
	}

	@Override
	public RowGroup<K, V, T> getRowGroup() {
		return rowGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result
				+ ((rowGroup == null) ? 0 : rowGroup.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfoContainerImpl<?, ?, ?> other = (NodeInfoContainerImpl<?, ?, ?>) obj;
		if (depth != other.depth)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (rowGroup == null) {
			if (other.rowGroup != null)
				return false;
		} else if (!rowGroup.equals(other.rowGroup))
			return false;
		return true;
	}

	@Override
	public int getDepth() {
		return depth;
	}
	
	@Override
	public String toString() {
		return "NodeInfoContainerImpl [node=" + node + ", rowGroup=" + rowGroup
				+ ", depth=" + depth + "]";
	}


}
