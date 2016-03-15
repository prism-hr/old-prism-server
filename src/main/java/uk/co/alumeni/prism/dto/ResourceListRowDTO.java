package uk.co.alumeni.prism.dto;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;

public class ResourceListRowDTO extends ResourceSimpleDTO implements ProfileEntityDTO {

    private Integer systemId;

    private Integer institutionId;

    private String institutionName;

    private Integer departmentId;

    private String departmentName;

    private Integer programId;

    private String programName;

    private Integer projectId;

    private String projectName;

    private Integer applicationId;

    private String applyHomepage;

    private Integer userId;

    private String userFirstName;

    private String userFirstName2;

    private String userFirstName3;

    private String userLastName;

    private String userEmail;

    private String userAccountImageUrl;

    private BigDecimal applicationRatingAverage;

    private List<PrismState> secondaryStateIds;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    private String advertIncompleteSection;

    private Long stateActionPendingCount;

    private List<ActionDTO> actions;

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

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
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

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
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

    public String getAdvertIncompleteSection() {
        return advertIncompleteSection;
    }

    public void setAdvertIncompleteSection(String advertIncompleteSection) {
        this.advertIncompleteSection = advertIncompleteSection;
    }

    public Long getStateActionPendingCount() {
        return stateActionPendingCount;
    }

    public void setStateActionPendingCount(Long stateActionPendingCount) {
        this.stateActionPendingCount = stateActionPendingCount;
    }

    public List<ActionDTO> getActions() {
        return actions;
    }

    public void setActions(List<ActionDTO> actions) {
        this.actions = actions;
    }

    public Integer getResourceId() {
        return ObjectUtils.firstNonNull(applicationId, projectId, programId, departmentId, institutionId, systemId);
    }

}
