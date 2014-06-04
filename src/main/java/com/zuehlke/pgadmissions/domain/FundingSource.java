package com.zuehlke.pgadmissions.domain;

public class FundingSource extends ImportedEntity {

    public FundingSource withId(Integer id) {
        setId(id);
        return this;
    }

    public FundingSource withCode(String code) {
        setCode(code);
        return this;
    }

    public FundingSource withName(String name) {
        setName(name);
        return this;
    }

}
