package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.Domicile;

public class ProgramOpportunityDTO {

    private String programCode;

    private String programName;

    private Boolean atasRequired;

    private Domicile institutionCountry;

    private String institutionCode;

    private String otherInstitution;

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

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Boolean getAtasRequired() {
        return atasRequired;
    }

    public void setAtasRequired(Boolean atasRequired) {
        this.atasRequired = atasRequired;
    }

    public Domicile getInstitutionCountry() {
        return institutionCountry;
    }

    public void setInstitutionCountry(Domicile institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getOtherInstitution() {
        return otherInstitution;
    }

    public void setOtherInstitution(String otherInstitution) {
        this.otherInstitution = otherInstitution;
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
