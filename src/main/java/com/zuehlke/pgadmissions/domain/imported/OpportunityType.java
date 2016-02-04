package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("OPPORTUNITY_TYPE")
public class OpportunityType extends ImportedEntitySimple {

    public OpportunityType withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public OpportunityType withCode(String code) {
        setCode(code);
        return this;
    }

    public OpportunityType withName(String name) {
        setName(name);
        return this;
    }

    public OpportunityType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismOpportunityType getPrismOpportunityType() {
        return PrismOpportunityType.valueOf(getCode());
    }

}
