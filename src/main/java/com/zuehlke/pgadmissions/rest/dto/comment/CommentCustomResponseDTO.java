package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.constraints.NotNull;

public class CommentCustomResponseDTO {

    @NotNull
    private Integer id;

    private String propertyValue;

    public  Integer getId() {
        return id;
    }

    public  void setId(Integer id) {
        this.id = id;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
