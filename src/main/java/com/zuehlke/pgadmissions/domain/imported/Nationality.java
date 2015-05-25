package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("NATIONALITY")
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
