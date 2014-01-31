package com.zuehlke.pgadmissions.dto;

import java.util.List;

public class ProgramOpportunityDTO {

    private String programCode;

    private String description;

    private Integer studyDuration;

    private String funding;

    private Boolean active;

    private List<String> studyOptions;

    private Integer advertiseDeadlineYear;

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<String> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Integer getAdvertiseDeadlineYear() {
        return advertiseDeadlineYear;
    }

    public void setAdvertiseDeadlineYear(Integer advertiseDeadlineYear) {
        this.advertiseDeadlineYear = advertiseDeadlineYear;
    }

}
