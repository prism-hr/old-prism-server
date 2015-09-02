package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentInvitationDTO;

public class ImportedProgramDTO extends ImportedEntityDTO {

    @NotNull
    private ImportedInstitutionDTO institution;
    
    @Valid
    private DepartmentInvitationDTO department;

    @NotNull
    private ImportedEntityDTO qualificationType;

    public ImportedInstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionDTO institution) {
        this.institution = institution;
    }

    public DepartmentInvitationDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentInvitationDTO department) {
        this.department = department;
    }

    public ImportedEntityDTO getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(ImportedEntityDTO qualificationType) {
        this.qualificationType = qualificationType;
    }

}
