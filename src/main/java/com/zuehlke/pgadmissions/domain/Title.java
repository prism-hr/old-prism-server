package com.zuehlke.pgadmissions.domain;

public class Title extends ImportedEntity {

    public Title withId(Integer id) {
        setId(id);
        return this;
    }

    public Title withCode(String code) {
        setCode(code);
        return this;
    }

    public Title withName(String name) {
        setName(name);
        return this;
    }

}
