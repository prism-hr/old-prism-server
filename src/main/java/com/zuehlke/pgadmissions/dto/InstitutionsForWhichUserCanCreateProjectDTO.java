package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.institution.Institution;

public class InstitutionsForWhichUserCanCreateProjectDTO {

    private Institution institution;

    private Boolean institutionPartnerMode;

    private Boolean programPartnerMode;

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Boolean getInstitutionPartnerMode() {
        return institutionPartnerMode;
    }

    public void setInstitutionPartnerMode(Boolean institutionPartnerMode) {
        this.institutionPartnerMode = institutionPartnerMode;
    }

    public Boolean getProgramPartnerMode() {
        return programPartnerMode;
    }

    public void setProgramPartnerMode(Boolean programPartnerMode) {
        this.programPartnerMode = programPartnerMode;
    }
    
    public InstitutionsForWhichUserCanCreateProjectDTO withInstitution(Institution insitution) {
        this.institution = insitution;
        return this;
    }
    
    public InstitutionsForWhichUserCanCreateProjectDTO withInstitutionPartnerMode(Boolean institutionPartnerMode) {
        this.institutionPartnerMode = institutionPartnerMode;
        return this;
    }
    
    public InstitutionsForWhichUserCanCreateProjectDTO withProgramPartnerMode(Boolean programPartnerMode) {
        this.programPartnerMode = programPartnerMode;
        return this;
    }

}
