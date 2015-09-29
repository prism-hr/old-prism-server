package com.zuehlke.pgadmissions.dto;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class UserAdvertDTO {

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

    public Integer getResourceId() {
        return firstNonNull(institutionId, departmentId, programId, projectId);
    }

}
