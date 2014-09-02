package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOMICILE")
public class Domicile extends SimpleImportedEntity {
    
    public Domicile withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public Domicile withCode(String code) {
        setCode(code);
        return this;
    }

    public Domicile withName(String name) {
        setName(name);
        return this;
    }
    
    public Domicile withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
