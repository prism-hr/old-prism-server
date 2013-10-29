package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class SendToPorticoDataDTO {

	private String applicationNumber;
	
    private List<Integer> refereesSendToPortico;
    
    private List<Integer> qualificationsSendToPortico;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
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