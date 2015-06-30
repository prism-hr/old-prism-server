package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("nationality")
public class Nationality extends ImportedEntitySimple {

    public Nationality withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Nationality withCode(String code) {
        setCode(code);
        return this;
    }

    public Nationality withName(String name) {
        setName(name);
        return this;
    }

    public Nationality withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
