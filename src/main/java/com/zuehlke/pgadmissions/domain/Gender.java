package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("GENDER")
public class Gender extends SimpleImportedEntity {

    public Gender withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Gender withCode(String code) {
        setCode(code);
        return this;
    }

    public Gender withName(String name) {
        setName(name);
        return this;
    }

    public Gender withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}