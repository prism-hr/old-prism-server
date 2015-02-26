package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public class AdvertRecommendationDTO {

    private Advert advert;
    
    private Long applicationCount;

    public final Advert getAdvert() {
        return advert;
    }

    public final void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final Long getApplicationCount() {
        return applicationCount;
    }

    public final void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }
    
}
