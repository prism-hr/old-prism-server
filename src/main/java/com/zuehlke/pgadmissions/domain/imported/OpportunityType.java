package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("opportunity_type")
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
