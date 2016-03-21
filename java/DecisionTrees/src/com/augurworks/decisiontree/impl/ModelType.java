package com.augurworks.decisiontree.impl;

import com.augurworks.decisiontree.Copyable;

public enum ModelType implements Copyable<ModelType> {
    NO_GOOD_PREDICTION("NO"),
    TWO_WEEK_DURING_DAY("2WDD"),
    FOUR_WEEK_DURING_DAY("4WDD"),
    THREE_MONTH_DURING_DAY("3MDD"),
    SIX_MONTH_DURING_DAY("6MDD"),
    TWO_WEEK_OR_FOUR_WEEK("2_OR_FOUR"),
    ;

    private final String serializedValue;

    private ModelType(String serializedValue) {
        this.serializedValue = serializedValue;
    }

    public static ModelType fromString(String enumString) {
        for (ModelType s : ModelType.values()) {
            if (s.serializedValue.equalsIgnoreCase(enumString)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unkown ModelType type: " + enumString);
    }

    @Override
    public ModelType copy() {
        return this;
    }

}
