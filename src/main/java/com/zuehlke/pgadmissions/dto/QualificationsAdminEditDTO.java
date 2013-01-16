package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;

public class QualificationsAdminEditDTO {
    
    private String applicationId;
    
    private ArrayList<String> qualificationSendToUcl = new ArrayList<String>();
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public ArrayList<String> getQualificationSendToUcl() {
        return qualificationSendToUcl;
    }

    public void setQualificationSendToUcl(ArrayList<String> qualificationSendToUcl) {
        this.qualificationSendToUcl = qualificationSendToUcl;
    }
}
