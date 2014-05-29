package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SOURCES_OF_INTEREST")
public class SourcesOfInterest extends ImportedEntity {

    public SourcesOfInterest withId(Integer id) {
        setId(id);
        return this;
    }

    public SourcesOfInterest withCode(String code) {
        setCode(code);
        return this;
    }

    public SourcesOfInterest withName(String name) {
        setName(name);
        return this;
    }
    
    public boolean isFreeText() {
        return getCode().equalsIgnoreCase("OTHER") || getCode().equalsIgnoreCase("NEWS_AD") || getCode().equalsIgnoreCase("OTH_ACAD ")
                || getCode().equalsIgnoreCase("OTH_WEB");
    }

}
