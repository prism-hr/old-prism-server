package uk.co.alumeni.prism.rest.dto.profile;

import javax.validation.constraints.Size;

public class ProfileAdditionalInformationDTO {

    @Size(max = 1000)
    private String requirements;

    @Size(max = 1000)
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
