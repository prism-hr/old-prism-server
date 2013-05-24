package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "PROGRAM_ADVERT")
public class ProgramAdvert implements Serializable {
    private static final long serialVersionUID = 5963260213501162814L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_of_study_in_month")
    private Integer durationOfStudyInMonth;

    @Column(name = "funding_information")
    private String fundingInformation;

    @Column(name = "is_currently_accepting_applications")
    private Boolean isCurrentlyAcceptingApplications;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationOfStudyInMonth() {
        return durationOfStudyInMonth;
    }

    public void setDurationOfStudyInMonth(Integer durationOfStudyInMonth) {
        this.durationOfStudyInMonth = durationOfStudyInMonth;
    }

    public String getFundingInformation() {
        return fundingInformation;
    }

    public void setFundingInformation(String fundingInformation) {
        this.fundingInformation = fundingInformation;
    }

    public Boolean getIsCurrentlyAcceptingApplications() {
        return isCurrentlyAcceptingApplications;
    }

    public void setIsCurrentlyAcceptingApplications(Boolean isCurrentlyAcceptingApplications) {
        this.isCurrentlyAcceptingApplications = isCurrentlyAcceptingApplications;
    }
}
