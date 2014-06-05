package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NATIONALITY")
public class Language extends ImportedEntity {

    public Language withCode(String code) {
        setCode(code);
        return this;
    }

    public Language withName(String name) {
        setName(name);
        return this;
    }

}
