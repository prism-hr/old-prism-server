package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ethnicity")
public class Ethnicity extends ImportedEntitySimple {

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

    public Ethnicity withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
