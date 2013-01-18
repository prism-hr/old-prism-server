package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;

public class QualificationsAdminEditDTO {
    
    private ArrayList<String> qualifications = new ArrayList<String>();
    
    public ArrayList<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(ArrayList<String> qualifications) {
        this.qualifications = qualifications;
    }
}
