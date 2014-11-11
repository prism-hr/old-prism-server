package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class CommentPropertyAnswerDTO {

    @NotNull
    private Integer version;
    
    @NotEmpty
    private List<Object> values;

    public final Integer getVersion() {
        return version;
    }

    public final void setVersion(Integer version) {
        this.version = version;
    }

    public final List<Object> getValues() {
        return values;
    }

    public final void setValues(List<Object> values) {
        this.values = values;
    }
    
}
