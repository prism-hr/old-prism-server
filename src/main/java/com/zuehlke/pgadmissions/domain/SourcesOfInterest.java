package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SOURCES_OF_INTEREST")
public class SourcesOfInterest extends ImportedEntity {

    public boolean isFreeText() {
        return getCode().equalsIgnoreCase("OTHER") || getCode().equalsIgnoreCase("NEWS_AD") || getCode().equalsIgnoreCase("OTH_ACAD ")
                || getCode().equalsIgnoreCase("OTH_WEB");
    }

}
