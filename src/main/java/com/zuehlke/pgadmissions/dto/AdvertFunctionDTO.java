package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;

public class AdvertFunctionDTO {

    private Integer advertId;

    private PrismAdvertFunction function;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public PrismAdvertFunction getFunction() {
        return function;
    }

    public void setFunction(PrismAdvertFunction function) {
        this.function = function;
    }

}
