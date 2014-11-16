package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ResourceListRowRepresentation {

    private Integer id;

    private Boolean raisesUpdateFlag;

    private String code;

    private UserRepresentation user;

    private SimpleResourceRepresentation institution;

    private SimpleResourceRepresentation program;

    private SimpleResourceRepresentation project;

    private LocalDate closingDate;

    private PrismStateGroup stateGroupId;

    private LocalDate dueDate;

    private DateTime updatedTimestamp;

    private Set<ActionRepresentation> actions;

    private BigDecimal applicationRatingAverage;

    private PrismScope resourceScope;

    private String sequenceIdentifier;

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

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public SimpleResourceRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(SimpleResourceRepresentation institution) {
        this.institution = institution;
    }

    public SimpleResourceRepresentation getProgram() {
        return program;
    }

    public void setProgram(SimpleResourceRepresentation program) {
        this.program = program;
    }

    public SimpleResourceRepresentation getProject() {
        return project;
    }

    public void setProject(SimpleResourceRepresentation project) {
        this.project = project;
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

    public Set<ActionRepresentation> getActions() {
        return actions;
    }

    public void setActions(Set<ActionRepresentation> actions) {
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

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }
}
