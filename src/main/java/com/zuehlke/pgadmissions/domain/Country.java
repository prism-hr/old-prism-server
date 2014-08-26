package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COUNTRY")
public class Country extends SimpleImportedEntity {

    public Country withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Country withCode(String code) {
        setCode(code);
        return this;
    }

    public Country withName(String name) {
        setName(name);
        return this;
    }

    public Country withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
