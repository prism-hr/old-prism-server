package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public class AdvertRecommendationDTO {

    private Advert advert;
    
    private boolean newAdvert;
    
    private Integer projectCount;
    
    private boolean newProjectAdvert;

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public boolean isNewAdvert() {
        return newAdvert;
    }

    public void setNewAdvert(boolean newAdvert) {
        this.newAdvert = newAdvert;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public boolean isNewProjectAdvert() {
        return newProjectAdvert;
    }

    public void setNewProjectAdvert(boolean newProjectAdvert) {
        this.newProjectAdvert = newProjectAdvert;
    }
    
    public AdvertRecommendationDTO withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }
    
}
