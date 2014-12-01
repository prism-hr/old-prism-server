package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("RESIDENCE_STATE")
public class ResidenceState extends SimpleImportedEntity {

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

    public ResidenceState withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
