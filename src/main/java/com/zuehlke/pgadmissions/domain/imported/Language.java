package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("NATIONALITY")
public class Language extends ImportedEntitySimple {

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
