package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("title")
public class Title extends ImportedEntitySimple {

    public Title withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Title withCode(String code) {
        setCode(code);
        return this;
    }

    public Title withName(String name) {
        setName(name);
        return this;
    }

    public Title withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
