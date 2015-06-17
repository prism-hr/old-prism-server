package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

@Entity
@DiscriminatorValue("OPPORTUNITY_TYPE")
public class ImportedOpportunityType extends ImportedEntitySimple {

    public ImportedOpportunityType withName(String name) {
        setName(name);
        return this;
    }

    public ImportedOpportunityType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismOpportunityType getPrismOpportunityType() {
        return PrismOpportunityType.valueOf(getName());
    }

}
