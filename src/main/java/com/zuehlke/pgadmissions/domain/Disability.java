package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DISABILITY")
public class Disability extends ImportedEntity {

    public Disability withCode(String code) {
        setCode(code);
        return this;
    }
    
    public Disability withName(String name) {
        setName(name);
        return this;
    }
}
