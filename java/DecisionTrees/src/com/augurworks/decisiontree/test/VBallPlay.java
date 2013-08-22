package com.augurworks.decisiontree.test;

import com.augurworks.decisiontree.CopyAble;

public enum VBallPlay implements CopyAble<VBallPlay> {
	YES,
	NO,
	;
	
	public static VBallPlay fromString(String enumString) {
		for (VBallPlay s : VBallPlay.values()) {
			if (s.name().equalsIgnoreCase(enumString)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unkown VBallPlay type: " + enumString);
	}

	@Override
	public VBallPlay copy() {
		return this;
	}
}
