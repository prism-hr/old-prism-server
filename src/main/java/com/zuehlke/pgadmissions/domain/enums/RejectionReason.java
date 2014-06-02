package com.zuehlke.pgadmissions.domain.enums;

public enum RejectionReason {
    
    APPLICATION_INCOMPLETE("We were unable to form a judgement on your suitability based upon the information supplied in your application."),
    
    NOT_QUALIFIED_FOR_INSTITUTION("Your qualifications and experience are not sufficient to satisfy the entrance requirements for a research degree programme at UCL."),
    
    NOT_QUALIFIED_FOR_OPPORTUNITY("Your qualifications and experience are not appropriate for the research degree programme that you applied for."),
    
    UNABLE_TO_FIND_SUPERVISOR("At the present time, we are unable to identify academic supervisors to support you in your preferred research programme."),
    
    DID_NOT_ATTENT_INTERVIEW("You failed to present for interview as arranged."),
    
    OPPORTUNITY_OVERSUBSCRIBED("Although you may be suitable for a research degree programme at UCL, the competition for places on the programme that you applied " +
            "for was such that we were unable to progress your application on this occasion. Subject to the continuation of the programme, you may reapply in the " +
            "next academic year."),
    
    OPPORTUNITY_NO_LONGER_AVAILABLE("We are no longer able to offer the programme that you applied for."),
    
    WITHDRAWN_BY_MUTUAL_CONSENT("You have informed us that you no longer wish to be considered.");
    
    private String reason;
    
    private RejectionReason(String reason) {
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
