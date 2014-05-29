package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COUNTRY")
public class Country extends ImportedEntity {

    public Country withCode(String code) {
        setCode(code);
        return this;
    }
    
    public Country withName(String name) {
        setName(name);
        return this;
    }
    
}
