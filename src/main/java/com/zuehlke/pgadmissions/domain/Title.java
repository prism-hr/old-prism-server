package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TITLE")
public class Title extends ImportedEntity {

    public Title withCode(String code) {
        setCode(code);
        return this;
    }

    public Title withName(String name) {
        setName(name);
        return this;
    }

}
