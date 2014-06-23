package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("FUNDING_SOURCE")
public class FundingSource extends ImportedEntity {

    public FundingSource withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }
    
    public FundingSource withCode(String code) {
        setCode(code);
        return this;
    }
    
    public FundingSource withName(String name) {
        setName(name);
        return this;
    }
    
    public FundingSource withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
