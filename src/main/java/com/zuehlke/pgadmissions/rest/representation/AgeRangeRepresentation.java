package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.PrismAgeRange;

public class AgeRangeRepresentation {

    private PrismAgeRange id;

    private Integer lowerBound;

    private Integer upperBound;
    
    public AgeRangeRepresentation(PrismAgeRange id, Integer lowerBound, Integer upperBound) {
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public PrismAgeRange getId() {
        return id;
    }

    public void setId(PrismAgeRange id) {
        this.id = id;
    }

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
