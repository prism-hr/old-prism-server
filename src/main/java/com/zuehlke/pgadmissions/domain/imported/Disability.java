package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("DISABILITY")
public class Disability extends ImportedEntitySimple {

    public Disability withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Disability withCode(String code) {
        setCode(code);
        return this;
    }

    public Disability withName(String name) {
        setName(name);
        return this;
    }

    public Disability withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
