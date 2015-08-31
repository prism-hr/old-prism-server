package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.NotNull;

public class ImportedEntityDTO {

    @NotNull
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImportedEntityDTO withId(Integer id) {
        this.id = id;
        return this;
    }

}
