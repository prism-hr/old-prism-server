package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ResourceListRowDTO {

    private Integer systemId;

    private Integer institutionId;

    private String institutionTitle;

    private Integer institutionLogoImageId;

    private Integer departmentId;

    private String departmentTitle;

    private Integer programId;

    private String programTitle;

    private String projectTitle;

    private Integer projectId;

    private Integer applicationId;

    private String code;

    private Integer userId;

    private String userFirstName;

    private String userFirstName2;

    private String userFirstName3;

    private String userLastName;

    private String userEmail;

    private String userAccountImageUrl;

    private BigDecimal applicationRatingAverage;

    private PrismState stateId;

    private List<PrismState> secondaryStateIds;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    private Set<ActionDTO> actions;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionTitle() {
        return institutionTitle;
    }

    public void setInstitutionTitle(String institutionTitle) {
        this.institutionTitle = institutionTitle;
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

    public String getDepartmentTitle() {
        return departmentTitle;
    }

    public void setDepartmentTitle(String departmentTitle) {
        this.departmentTitle = departmentTitle;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getUserAccountImageUrl() {
        return userAccountImageUrl;
    }

    public void setUserAccountImageUrl(String creatorAccountImageUrl) {
        this.userAccountImageUrl = creatorAccountImageUrl;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public PrismState getStateId() {
        return stateId;
    }

    public void setStateId(PrismState stateId) {
        this.stateId = stateId;
    }

    public List<PrismState> getSecondaryStateIds() {
        return secondaryStateIds;
    }

    public void setSecondaryStateIds(List<PrismState> secondaryStateIds) {
        this.secondaryStateIds = secondaryStateIds;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public Set<ActionDTO> getActions() {
        return actions;
    }

    public void setActions(Set<ActionDTO> actions) {
        this.actions = actions;
    }

    public Integer getResourceId() {
        return ObjectUtils.firstNonNull(applicationId, projectId, programId, departmentId, institutionId);
    }

}
