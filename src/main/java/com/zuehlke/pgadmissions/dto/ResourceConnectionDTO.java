package com.zuehlke.pgadmissions.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

public class ResourceConnectionDTO implements Comparable<ResourceConnectionDTO> {

    private Integer institutionId;

    private String institutionName;

    private Integer logoImageId;

    private Integer departmentId;

    private String departmentName;

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Integer getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(Integer logoImageId) {
        this.logoImageId = logoImageId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institutionId, departmentId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceConnectionDTO other = (ResourceConnectionDTO) object;
        return Objects.equal(institutionId, other.getInstitutionId()) && Objects.equal(departmentId, other.getDepartmentId());
    }

    @Override
    public int compareTo(ResourceConnectionDTO other) {
        int compare = ObjectUtils.compare(institutionName, other.getInstitutionName());
        return compare == 0 ? ObjectUtils.compare(departmentName, other.getDepartmentName()) : compare;
    }
    
}
