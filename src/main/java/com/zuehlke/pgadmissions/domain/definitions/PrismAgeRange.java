package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismAgeRange implements PrismLocalizableDefinition {

    FROM_0_TO_18(null, 18), //
    FROM_19_TO_24(19, 24), //
    FROM_25_TO_29(25, 219), //
    FROM_30_TO_39(30, 39), //
    FROM_40_TO_49(40, 49), //
    FROM_50_TO_59(50, 59), //
    FROM_60_ONWARDS(60, null);

    private Integer lowerBound;

    private Integer upperBound;

    private PrismAgeRange(Integer lowerBound, Integer upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_AGE_RANGE_" + this.name());
    }

}
