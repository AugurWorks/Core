package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.Copyable;

public class CopyableString implements Copyable<CopyableString> {
	private static final long serialVersionUID = 1L;
	private final String string;
	
	public CopyableString(String s) {
		this.string = s;
	}

	@Override
	public CopyableString copy() {
		return this;
	}
	
	public String getString() {
		return string;
	}
	
	public static CopyableString from(String s) {
		return new CopyableString(s);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
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
		CopyableString other = (CopyableString) obj;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}
}
