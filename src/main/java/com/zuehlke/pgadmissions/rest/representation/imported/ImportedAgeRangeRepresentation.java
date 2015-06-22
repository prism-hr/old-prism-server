package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedAgeRangeRepresentation extends ImportedEntitySimpleRepresentation {

    private Integer lowerBound;

    private Integer upperBound;

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

}
