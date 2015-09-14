package com.zuehlke.pgadmissions.rest.dto.imported;

import org.hibernate.validator.constraints.NotEmpty;

public class ImportedDomicileDTO {

    @NotEmpty
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImportedDomicileDTO withId(final String id) {
        this.id = id;
        return this;
    }

    public ImportedDomicileDTO withName(final String name) {
        this.name = name;
        return this;
    }

}
