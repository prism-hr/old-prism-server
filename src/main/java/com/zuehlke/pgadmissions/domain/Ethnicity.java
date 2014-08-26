package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ETHNICITY")
public class Ethnicity extends SimpleImportedEntity {

    public Ethnicity withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Ethnicity withCode(String code) {
        setCode(code);
        return this;
    }

    public Ethnicity withName(String name) {
        setName(name);
        return this;
    }

    public Ethnicity withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
