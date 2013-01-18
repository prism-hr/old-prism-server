package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

public class RefereesAdminEditJsonDTO {

    private String applicationId;
    
    private List<String> refereeSendToUcl = new ArrayList<String>();

    public RefereesAdminEditJsonDTO() {
    }
    
    public List<String> getRefereeSendToUcl() {
        return refereeSendToUcl;
    }
    
    public void setRefereeSendToUcl(List<String> refereeSendToUcl) {
        this.refereeSendToUcl = refereeSendToUcl;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
