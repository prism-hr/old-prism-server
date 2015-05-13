package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("AGE_RANGE")
public class AgeRange extends ImportedEntitySimple {

    public AgeRange withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public AgeRange withCode(String code) {
        setCode(code);
        return this;
    }

    public AgeRange withName(String name) {
        setName(name);
        return this;
    }

    public AgeRange withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
