package com.zuehlke.pgadmissions.dto;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class ConfirmSupervisionDTO {

    private Boolean confirmedSupervision;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String declinedSupervisionReason;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String projectTitle;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    @ATASConstraint
    private String projectAbstract;

    private LocalDate recommendedStartDate;

    private Boolean recommendedConditionsAvailable;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 1000)
    private String recommendedConditions;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String secondarySupervisorFirstName;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    private String secondarySupervisorLastName;
    
    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String secondarySupervisorEmail;
    

    public Boolean getConfirmedSupervision() {
        return confirmedSupervision;
    }

    public void setConfirmedSupervision(Boolean confirmedSupervision) {
        this.confirmedSupervision = confirmedSupervision;
    }

    public String getDeclinedSupervisionReason() {
        return declinedSupervisionReason;
    }

    public void setDeclinedSupervisionReason(String declinedSupervisionReason) {
        this.declinedSupervisionReason = declinedSupervisionReason;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectAbstract() {
        return projectAbstract;
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public LocalDate getRecommendedStartDate() {
        return recommendedStartDate;
    }

    public void setRecommendedStartDate(LocalDate recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
    }

    public Boolean getRecommendedConditionsAvailable() {
        return recommendedConditionsAvailable;
    }

    public void setRecommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
    }

    public String getRecommendedConditions() {
        return recommendedConditions;
    }

    public void setRecommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }

	public String getSecondarySupervisorFirstName() {
		return secondarySupervisorFirstName;
	}

	public void setSecondarySupervisorFirstName(String secondarySupervisorFirstName) {
		this.secondarySupervisorFirstName = secondarySupervisorFirstName;
	}

	public String getSecondarySupervisorLastName() {
		return secondarySupervisorLastName;
	}

	public void setSecondarySupervisorLastName(String secondarySupervisorLastName) {
		this.secondarySupervisorLastName = secondarySupervisorLastName;
	}

	public String getSecondarySupervisorEmail() {
		return secondarySupervisorEmail;
	}

	public void setSecondarySupervisorEmail(String secondarySupervisorEmail) {
		this.secondarySupervisorEmail = secondarySupervisorEmail;
	}

}