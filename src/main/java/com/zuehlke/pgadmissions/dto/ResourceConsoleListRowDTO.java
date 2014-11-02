package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceConsoleListRowDTO {

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

    private String code;

    private String institutionTitle;

    private String programTitle;

    private String projectTitle;

    private BigDecimal applicationRatingAverage;

    private PrismState stateId;

    private PrismStateGroup stateGroupId;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    public final Integer getSystemId() {
        return systemId;
    }

    public final void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public final Integer getInstitutionId() {
        return institutionId;
    }

    public final void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public final Integer getProgramId() {
        return programId;
    }

    public final void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public final Integer getProjectId() {
        return projectId;
    }

    public final void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final Integer getCreatorId() {
        return creatorId;
    }

    public final void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public final String getCreatorFirstName() {
        return creatorFirstName;
    }

    public final void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public final String getCreatorFirstName2() {
        return creatorFirstName2;
    }

    public final void setCreatorFirstName2(String creatorFirstName2) {
        this.creatorFirstName2 = creatorFirstName2;
    }

    public final String getCreatorFirstName3() {
        return creatorFirstName3;
    }

    public final void setCreatorFirstName3(String creatorFirstName3) {
        this.creatorFirstName3 = creatorFirstName3;
    }

    public final String getCreatorLastName() {
        return creatorLastName;
    }

    public final void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }

    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final String getInstitutionTitle() {
        return institutionTitle;
    }

    public final void setInstitutionTitle(String institutionTitle) {
        this.institutionTitle = institutionTitle;
    }

    public final String getProgramTitle() {
        return programTitle;
    }

    public final void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public final String getProjectTitle() {
        return projectTitle;
    }

    public final void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public final BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public final void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public final PrismState getStateId() {
        return stateId;
    }

    public final void setStateId(PrismState stateId) {
        this.stateId = stateId;
    }

    public final PrismStateGroup getStateGroupId() {
        return stateGroupId;
    }

    public final void setStateGroupId(PrismStateGroup stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public final String getCreatorEmail() {
        return creatorEmail;
    }

    public final void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public final DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public final void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }
}
