package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

public class AdvertIndustryDTO {

    private Integer advertId;

    private PrismAdvertIndustry industry;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public PrismAdvertIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
    }

}
