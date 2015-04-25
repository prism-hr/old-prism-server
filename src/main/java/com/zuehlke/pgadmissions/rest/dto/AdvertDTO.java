package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class AdvertDTO {
    
    private PrismOpportunityType advertType;

    @NotEmpty
    @Size(max = 255)
    private String title;
    
    @NotEmpty
    @Size(max = 1000)
    private String summary;
    
    private String description;
    
    @URL
    @Size(max = 2048)
    private String applyHomepage;
    
    private FileDTO logoImage;

    private FileDTO backgroundImage;
    
    private List<String> locations = Lists.newArrayList();
    
    private LocalDate endDate;
    
    public PrismOpportunityType getAdvertType() {
        return advertType;
    }

    public void setAdvertType(PrismOpportunityType advertType) {
        this.advertType = advertType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public FileDTO getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileDTO logoImage) {
        this.logoImage = logoImage;
    }

    public FileDTO getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(FileDTO backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
}
