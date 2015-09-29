package com.zuehlke.pgadmissions.rest.dto.profile;

public class ProfileAdditionalInformationDTO {

    private String requirements;

    private String convictions;

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getConvictions() {
        return convictions;
    }

    public void setConvictions(String convictions) {
        this.convictions = convictions;
    }
}
