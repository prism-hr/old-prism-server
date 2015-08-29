package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class ApplicationPrizeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private String provider;

    private String title;

    private String description;

    private LocalDate awardDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final String getProvider() {
        return provider;
    }

    public final void setProvider(String provider) {
        this.provider = provider;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }
    
    public ApplicationPrizeRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationPrizeRepresentation withProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public ApplicationPrizeRepresentation withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ApplicationPrizeRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ApplicationPrizeRepresentation withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }
    
    public ApplicationPrizeRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    

}
