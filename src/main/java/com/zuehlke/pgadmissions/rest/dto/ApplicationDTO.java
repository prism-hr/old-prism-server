package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class ApplicationDTO {

    @NotNull
    private Integer advertId;
    
    @NotNull
    private Boolean doRetain;

    public final Integer getAdvertId() {
        return advertId;
    }

    public final void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public final Boolean getDoRetain() {
        return doRetain;
    }

    public final void setDoRetain(Boolean doRetain) {
        this.doRetain = doRetain;
    }

}
