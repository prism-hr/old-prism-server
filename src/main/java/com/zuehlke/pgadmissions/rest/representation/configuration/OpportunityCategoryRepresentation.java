package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class OpportunityCategoryRepresentation {

    private PrismOpportunityCategory id;

    private boolean hasFee;

    private boolean hasPay;

    private List<PrismOpportunityType> opportunityTypes;

    public PrismOpportunityCategory getId() {
        return id;
    }

    public void setId(PrismOpportunityCategory id) {
        this.id = id;
    }

    public boolean isHasFee() {
        return hasFee;
    }

    public void setHasFee(boolean hasFee) {
        this.hasFee = hasFee;
    }

    public boolean isHasPay() {
        return hasPay;
    }

    public void setHasPay(boolean hasPay) {
        this.hasPay = hasPay;
    }

    public List<PrismOpportunityType> getOpportunityTypes() {
        return opportunityTypes;
    }

    public void setOpportunityTypes(List<PrismOpportunityType> opportunityTypes) {
        this.opportunityTypes = opportunityTypes;
    }

}
