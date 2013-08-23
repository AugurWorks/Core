package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.TypeOperatorLimit;

public class TypeOperatorLimitImpl<K,V> implements TypeOperatorLimit<K,V> {
	private K type;
	private V limit;
	private BinaryOperator<V> operator;
	
	public TypeOperatorLimitImpl(K type, V limit, BinaryOperator<V> operator) {
		this.type = type;
		this.limit = limit;
		this.operator = operator;
	}

	@Override
	public K getType() {
		return type;
	}

	@Override
	public V getLimit() {
		return limit;
	}

	@Override
	public BinaryOperator<V> getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return "TypeOperatorLimitImpl [type=" + type + ", limit=" + limit
				+ ", operator=" + operator + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((limit == null) ? 0 : limit.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TypeOperatorLimitImpl<?, ?> other = (TypeOperatorLimitImpl<?, ?>) obj;
		if (limit == null) {
			if (other.limit != null)
				return false;
		} else if (!limit.equals(other.limit))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
