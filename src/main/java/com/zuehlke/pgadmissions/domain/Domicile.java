package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOMICILE")
public class Domicile extends ImportedEntity {

    public Domicile withId(Integer id) {
        setId(id);
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
    
}
