package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

public class QualificationsAdminEditDTO {
    
    private List<String> qualifications = new ArrayList<String>();
    
    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }
}
