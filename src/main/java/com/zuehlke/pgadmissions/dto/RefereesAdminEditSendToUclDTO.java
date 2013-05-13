package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

public class RefereesAdminEditSendToUclDTO {

    private List<String> referees = new ArrayList<String>();

    public RefereesAdminEditSendToUclDTO() {
    }
    
    public List<String> getReferees() {
        return referees;
    }
    
    public void setReferees(List<String> referees) {
        this.referees = referees;
    }

}
