package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NATIONALITY")
public class Language extends AbstractImportedEntity {

    public Language withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Language withCode(String code) {
        setCode(code);
        return this;
    }

    public Language withName(String name) {
        setName(name);
        return this;
    }

    public Language withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
