package com.zuehlke.pgadmissions.dto;

import java.util.List;

public class SendToPorticoDataDTO {

    private List<Integer> refereesSendToPortico;
    
    private List<Integer> qualificationsSendToPortico;

    private String emptyQualificationsExplanation;
    
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