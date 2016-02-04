package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("FUNDING_SOURCE")
public class FundingSource extends ImportedEntitySimple {

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

    public FundingSource withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
