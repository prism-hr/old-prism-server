package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceListRowDTO {

    private Integer systemId;

    private Integer institutionId;

    private Integer programId;

    private Integer projectId;

    private Integer applicationId;

    private Integer creatorId;

    private String creatorFirstName;

    private String creatorFirstName2;

    private String creatorFirstName3;

    private String creatorLastName;

    private String creatorEmail;

    private String creatorLinkedinProfileUrl;

    private String creatorAccountImageUrl;

    private String code;

    private String institutionTitle;

    private Integer institutionLogoImageId;

    private String partnerTitle;

    private Integer partnerLogoImageId;

    private String programTitle;

    private String projectTitle;

    private BigDecimal applicationRatingAverage;

    private PrismState stateId;

    private PrismStateGroup stateGroupId;

    private List<PrismStateGroup> secondaryStateGroupIds;

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

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public String getCreatorFirstName2() {
        return creatorFirstName2;
    }

    public void setCreatorFirstName2(String creatorFirstName2) {
        this.creatorFirstName2 = creatorFirstName2;
    }

    public String getCreatorFirstName3() {
        return creatorFirstName3;
    }

    public void setCreatorFirstName3(String creatorFirstName3) {
        this.creatorFirstName3 = creatorFirstName3;
    }

    public String getCreatorLastName() {
        return creatorLastName;
    }

    public void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getPartnerTitle() {
        return partnerTitle;
    }

    public void setPartnerTitle(String partnerTitle) {
        this.partnerTitle = partnerTitle;
    }

    public Integer getPartnerLogoImageId() {
        return partnerLogoImageId;
    }

    public void setPartnerLogoImageId(Integer partnerLogoImageId) {
        this.partnerLogoImageId = partnerLogoImageId;
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

    public PrismStateGroup getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(PrismStateGroup stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public List<PrismStateGroup> getSecondaryStateGroupIds() {
        return secondaryStateGroupIds;
    }

    public void setSecondaryStateGroupIds(List<PrismStateGroup> secondaryStateGroupIds) {
        this.secondaryStateGroupIds = secondaryStateGroupIds;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorLinkedinProfileUrl() {
        return creatorLinkedinProfileUrl;
    }

    public void setCreatorLinkedinProfileUrl(String creatorLinkedinProfileUrl) {
        this.creatorLinkedinProfileUrl = creatorLinkedinProfileUrl;
    }

    public String getCreatorAccountImageUrl() {
        return creatorAccountImageUrl;
    }

    public void setCreatorAccountImageUrl(String creatorAccountImageUrl) {
        this.creatorAccountImageUrl = creatorAccountImageUrl;
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
        return ObjectUtils.firstNonNull(applicationId, projectId, programId, institutionId, systemId);
    }

}
