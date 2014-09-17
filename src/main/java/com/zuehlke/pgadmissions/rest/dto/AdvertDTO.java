package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @Size(max = 20000)
    private String description;

    @URL
    @Size(max = 2048)
    private String applyLink;

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
}
