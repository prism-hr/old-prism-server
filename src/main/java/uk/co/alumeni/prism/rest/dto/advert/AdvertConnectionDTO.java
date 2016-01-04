package uk.co.alumeni.prism.rest.dto.advert;

import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;

public class AdvertConnectionDTO {

    private PrismResourceContext context;

    private Integer advertId;

    public PrismResourceContext getContext() {
        return context;
    }

    public void setContext(PrismResourceContext context) {
        this.context = context;
    }

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

}
