package com.zuehlke.pgadmissions.rest.dto.imported;

import org.hibernate.validator.constraints.NotEmpty;

public class ImportedAdvertDomicileDTO {

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

    public ImportedAdvertDomicileDTO withId(final String id) {
        this.id = id;
        return this;
    }

}
