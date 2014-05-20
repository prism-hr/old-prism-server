package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ETHNICITY")
public class Ethnicity extends ImportedEntity {

    public Ethnicity withId(Integer id) {
        setId(id);
        return this;
    }
    
    public Ethnicity withCode(String code) {
        setCode(code);
        return this;
    }
    
    public Ethnicity withName(String name) {
        setName(name);
        return this;
    }
    
    
}
