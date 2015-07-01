package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class ImportedEntitySimpleDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 255)
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

}
