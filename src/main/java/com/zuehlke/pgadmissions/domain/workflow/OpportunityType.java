package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.Definition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

@Entity
@Table(name = "opportunityType")
public class OpportunityType extends Definition<PrismOpportunityType> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismOpportunityType id;

    @Column(name = "opportunity_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismOpportunityCategory opportunityCategory;

    @Column(name = "published", nullable = false)
    private Boolean published;
    
    @Column(name = "require_endorsement", nullable = false)
    private Boolean requireEndorsement;
    
    @Override
    public PrismOpportunityType getId() {
        return id;
    }

    @Override
    public void setId(PrismOpportunityType id) {
        this.id = id;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }
    
    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getRequireEndorsement() {
        return requireEndorsement;
    }

    public void setRequireEndorsement(Boolean requireEndorsement) {
        this.requireEndorsement = requireEndorsement;
    }

    public OpportunityType withId(PrismOpportunityType id) {
        this.id = id;
        return this;
    }

    public OpportunityType withOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
        return this;
    }
    
    public OpportunityType withPublished(Boolean published) {
        this.published = published;
        return this;
    }
    
    public OpportunityType withRequireEndorsement(Boolean requireEndorsement) {
        this.requireEndorsement = requireEndorsement;
        return this;
    }

}
