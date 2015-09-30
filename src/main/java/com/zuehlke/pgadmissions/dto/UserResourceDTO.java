package com.zuehlke.pgadmissions.dto;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class UserResourceDTO {

    private Integer institutionId;

    private Integer departmentId;

    private Integer programId;

    private Integer projectId;

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public ResourceIdentityDTO getResourceDescriptor() {
        for (PrismScope scope : new PrismScope[] { INSTITUTION, DEPARTMENT, PROGRAM, PROJECT }) {
            Integer resourceId = (Integer) getProperty(this, scope.getLowerCamelName() + "Id");
            if (resourceId != null) {
                return new ResourceIdentityDTO().withScope(scope).withId(resourceId);
            }
        }
        return null;
    }

}
