package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;

public class AdvertBuilder {

    private Integer id;
    private String title;
    private String description;
    private Integer studyDuration;
    private String funding;
    private AdvertState state;
    private User contactUser;
    private AdvertClosingDate closingDate;
    private List<AdvertClosingDate> closingDates = new ArrayList<AdvertClosingDate>();

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

    public AdvertBuilder state(AdvertState state) {
        this.state = state;
        return this;
    }
    
    public AdvertBuilder contactUser(User contactUser) {
        this.contactUser = contactUser;
        return this;
    }
    
    public AdvertBuilder closingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
        return this;
    }
    
    public AdvertBuilder closingDates(AdvertClosingDate... closingDates) {
        this.closingDates.addAll(Arrays.asList(closingDates));
        return this;
    }

    public Advert build() {
        Advert advert = new ConcreteAdvert();
        advert.setId(id);
        advert.setTitle(title);
        advert.setDescription(description);
        advert.setStudyDuration(studyDuration);
        advert.setFunding(funding);
        advert.setState(state);
        advert.setContactUser(contactUser);
        advert.setClosingDate(closingDate);
        advert.getClosingDates().addAll(closingDates);
        return advert;
    }
}
