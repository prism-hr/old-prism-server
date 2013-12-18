package com.zuehlke.pgadmissions.dto;

import java.util.List;

import javax.validation.constraints.Size;

public class SendToPorticoDataDTO {

	private String applicationNumber;
	
    private List<Integer> refereesSendToPortico;
    
    private List<Integer> qualificationsSendToPortico;

    @Size(max = 500, message = "A maximum of 500 characters are allowed.")
    private String emptyQualificationsExplanation;
    
    public String getApplicationNumber() {
    	return applicationNumber;
    }
    
    public void setApplicationNumber(String applicationNumber) {
    	this.applicationNumber = applicationNumber;
    }
    
    public List<Integer> getRefereesSendToPortico() {
        return refereesSendToPortico;
    }

    public void setRefereesSendToPortico(List<Integer> referees) {
        this.refereesSendToPortico = referees;
    }

    public List<Integer> getQualificationsSendToPortico() {
        return qualificationsSendToPortico;
    }

    public void setQualificationsSendToPortico(List<Integer> qualifications) {
        this.qualificationsSendToPortico = qualifications;
    }

    public String getEmptyQualificationsExplanation() {
        return emptyQualificationsExplanation;
    }

    public void setEmptyQualificationsExplanation(String emptyQualificationsExplanation) {
        this.emptyQualificationsExplanation = emptyQualificationsExplanation;
    }

}