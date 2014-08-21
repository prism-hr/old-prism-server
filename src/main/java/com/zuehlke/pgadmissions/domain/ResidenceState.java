package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RESIDENCE_STATE")
public class ResidenceState extends AbstractImportedEntity {

    public ResidenceState withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public ResidenceState withCode(String code) {
        setCode(code);
        return this;
    }

    public ResidenceState withName(String name) {
        setName(name);
        return this;
    }

    public ResidenceState withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
