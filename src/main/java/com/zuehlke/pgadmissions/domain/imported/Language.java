package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NATIONALITY")
public class Language extends SimpleImportedEntity {

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

    public Language withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
