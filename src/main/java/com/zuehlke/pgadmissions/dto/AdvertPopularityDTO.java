package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public class AdvertPopularityDTO {

    private Advert advert;

    private Long applicationCount;

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final Long getApplicationCount() {
        return applicationCount;
    }

    public final void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }

}
