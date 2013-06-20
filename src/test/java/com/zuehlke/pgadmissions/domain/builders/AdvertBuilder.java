package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Advert;

public class AdvertBuilder {

    private Integer id;
    private String title;
    private String description;
    private Integer studyDuration;
    private String funding;
    private Boolean active = true;

    public AdvertBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertBuilder title(String title) {
        this.title = title;
        return this;
    }

    public AdvertBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AdvertBuilder studyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
        return this;
    }

    public AdvertBuilder funding(String funding) {
        this.funding = funding;
        return this;
    }

    public AdvertBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public Advert build() {
        Advert advert = new Advert();
        advert.setId(id);
        advert.setTitle(title);
        advert.setDescription(description);
        advert.setStudyDuration(studyDuration);
        advert.setFunding(funding);
        advert.setActive(active);
        return advert;
    }
}
