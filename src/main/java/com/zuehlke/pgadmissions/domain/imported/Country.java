package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("COUNTRY")
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
