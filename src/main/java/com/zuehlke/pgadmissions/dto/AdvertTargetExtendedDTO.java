package com.zuehlke.pgadmissions.dto;

public class AdvertTargetExtendedDTO extends AdvertTargetDTO {

    private Integer acceptInstitutionId;

    private String acceptInstitutionName;

    private Integer acceptLogoImageId;

    private Integer acceptDepartmentId;

    private String acceptDepartmentName;

    public Integer getAcceptInstitutionId() {
        return acceptInstitutionId;
    }

    public void setAcceptInstitutionId(Integer acceptInstitutionId) {
        this.acceptInstitutionId = acceptInstitutionId;
    }

    public String getAcceptInstitutionName() {
        return acceptInstitutionName;
    }

    public void setAcceptInstitutionName(String acceptInstitutionName) {
        this.acceptInstitutionName = acceptInstitutionName;
    }

    public Integer getAcceptLogoImageId() {
        return acceptLogoImageId;
    }

    public void setAcceptLogoImageId(Integer acceptLogoImageId) {
        this.acceptLogoImageId = acceptLogoImageId;
    }

    public Integer getAcceptDepartmentId() {
        return acceptDepartmentId;
    }

    public void setAcceptDepartmentId(Integer acceptDepartmentId) {
        this.acceptDepartmentId = acceptDepartmentId;
    }

    public String getAcceptDepartmentName() {
        return acceptDepartmentName;
    }

    public void setAcceptDepartmentName(String acceptDepartmentName) {
        this.acceptDepartmentName = acceptDepartmentName;
    }

}
