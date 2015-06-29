package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ResourceListRowRepresentation {

    private Integer id;

    private Boolean raisesUpdateFlag;

    private String code;

    private UserRepresentation user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple partner;

    private ResourceRepresentationSimple program;

    private ResourceRepresentationSimple project;

    private LocalDate closingDate;

    private PrismStateGroup stateGroup;

    private List<PrismStateGroup> secondaryStateGroups;

    private LocalDate dueDate;

    private DateTime createdTimestamp;
    
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

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimple getPartner() {
        return partner;
    }

    public void setPartner(ResourceRepresentationSimple partner) {
        this.partner = partner;
    }

    public ResourceRepresentationSimple getProgram() {
        return program;
    }

    public void setProgram(ResourceRepresentationSimple program) {
        this.program = program;
    }

    public ResourceRepresentationSimple getProject() {
        return project;
    }

    public void setProject(ResourceRepresentationSimple project) {
        this.project = project;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(PrismStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public final List<PrismStateGroup> getSecondaryStateGroups() {
        return secondaryStateGroups;
    }

    public final void setSecondaryStateGroups(List<PrismStateGroup> secondaryStateGroups) {
        this.secondaryStateGroups = secondaryStateGroups;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
