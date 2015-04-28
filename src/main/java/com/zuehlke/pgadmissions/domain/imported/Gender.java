package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("GENDER")
public class Gender extends ImportedEntitySimple {

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

    public Gender withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
