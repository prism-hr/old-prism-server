package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class ApplicationDTO {

    @NotNull
    private Integer advertId;

    public final Integer getAdvertId() {
        return advertId;
    }

    public final void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

}
