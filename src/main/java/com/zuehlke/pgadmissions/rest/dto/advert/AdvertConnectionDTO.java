package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;

public class AdvertConnectionDTO {

    private PrismMotivationContext context;

    private Integer advertId;

    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext context) {
        this.context = context;
    }

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }
    
}
