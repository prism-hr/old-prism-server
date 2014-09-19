package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;

public class ResourceListRowRepresentation {

    private Integer id;
    
    private Boolean raisesUpdateFlag;

    private String code;

    private UserExtendedRepresentation user;

    private String institutionTitle;

    private String programTitle;

    private String projectTitle;

    private LocalDate closingDate;

    private PrismStateGroup stateGroupId;

    private LocalDate dueDate;

    private DateTime updatedTimestamp;

    private List<ActionRepresentation> actions;

    private BigDecimal applicationRatingAverage;

    private PrismScope resourceScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final Boolean getRaisesUpdateFlag() {
        return raisesUpdateFlag;
    }

    public final void setRaisesUpdateFlag(Boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserExtendedRepresentation getUser() {
        return user;
    }

    public void setUser(UserExtendedRepresentation user) {
        this.user = user;
    }

    public String getInstitutionTitle() {
        return institutionTitle;
    }

    public void setInstitutionTitle(String institutionTitle) {
        this.institutionTitle = institutionTitle;
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

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public PrismStateGroup getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(PrismStateGroup stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public List<ActionRepresentation> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentation> actions) {
        this.actions = actions;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

}
