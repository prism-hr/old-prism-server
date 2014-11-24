package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class CommentCustomResponseDTO {

    @NotNull
    private Integer id;

    @NotNull
    private String value;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

}
