package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DISABILITY")
public class Disability extends AbstractImportedEntity {

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

    public Disability withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
