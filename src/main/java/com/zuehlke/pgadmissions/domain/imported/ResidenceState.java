package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("residence_state")
public class ResidenceState extends ImportedEntitySimple {

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
