package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class AdvertBuilder {

    private Integer id;
    private String title;
    private String description;
    private Integer studyDuration;
    private String funding;
    private Boolean active = true;
    private Boolean enabled = true;
    private Date lastEditedTimestamp = new Date();
    private RegisteredUser contactUser;

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
    
    public AdvertBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdvertBuilder lastEditedTimestamp(Date lastEditedTimestamp) {
        this.lastEditedTimestamp = lastEditedTimestamp;
        return this;
    }
    
    public AdvertBuilder contactUser(RegisteredUser contactUser) {
        this.contactUser = contactUser;
        return this;
    }

    public Advert build() {
        Advert advert = new ConcreteAdvert();
        advert.setId(id);
        advert.setTitle(title);
        advert.setDescription(description);
        advert.setStudyDuration(studyDuration);
        advert.setFunding(funding);
        advert.setActive(active);
        advert.setEnabled(enabled);
        advert.setLastEditedTimestamp(lastEditedTimestamp);
        advert.setContactUser(contactUser);
        return advert;
    }
}