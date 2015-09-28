package com.zuehlke.pgadmissions.dto;

public class AdvertTargetDTO {

    private Integer userId;

    private String userFirstName;

    private String userFirstName2;

    private String userFirstName3;

    private String userLastName;

    private String userEmail;

    private String userAccountProfileUrl;

    private String userAccountImageUrl;

    private String userPortraitImageId;

    private Integer advertId;

    private Integer institutionId;

    private String institutionName;

    private Integer institutionLogoImageId;

    private Integer departmentId;

    private String departmentName;

    private Boolean accepted;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserFirstName2() {
        return userFirstName2;
    }

    public void setUserFirstName2(String userFirstName2) {
        this.userFirstName2 = userFirstName2;
    }

    public String getUserFirstName3() {
        return userFirstName3;
    }

    public void setUserFirstName3(String userFirstName3) {
        this.userFirstName3 = userFirstName3;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAccountProfileUrl() {
        return userAccountProfileUrl;
    }

    public void setUserAccountProfileUrl(String userAccountProfileUrl) {
        this.userAccountProfileUrl = userAccountProfileUrl;
    }

    public String getUserAccountImageUrl() {
        return userAccountImageUrl;
    }

    public void setUserAccountImageUrl(String userAccountImageUrl) {
        this.userAccountImageUrl = userAccountImageUrl;
    }

    public String getUserPortraitImageId() {
        return userPortraitImageId;
    }

    public void setUserPortraitImageId(String userPortraitImageId) {
        this.userPortraitImageId = userPortraitImageId;
    }

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

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

    public Integer getInstitutionLogoImageId() {
        return institutionLogoImageId;
    }

    public void setInstitutionLogoImageId(Integer institutionLogoImageId) {
        this.institutionLogoImageId = institutionLogoImageId;
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

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

}
