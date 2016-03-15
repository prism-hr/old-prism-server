package uk.co.alumeni.prism.rest.dto;

import javax.validation.constraints.NotNull;

public class EntityDTO {

    @NotNull
    public Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
