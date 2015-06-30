package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("country")
public class Country extends ImportedEntitySimple {

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

    public Country withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
