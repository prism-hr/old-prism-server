package com.zuehlke.pgadmissions.rest.representation.resource;

import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ResourceRepresentationStandard extends ResourceRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceRepresentationSimple program;

    private ResourceRepresentationSimple project;

    private boolean raisesUrgentFlag;

    private boolean raisesUpdateFlag;

    private BigDecimal applicationRatingAverage;

    private BigDecimal opportunityRatingAverage;

    private StateRepresentationSimple previousState;

    private List<StateRepresentationSimple> secondaryStates;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    private List<PrismScopeRequiredSection> advertIncompleteSections;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimple getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimple department) {
        this.department = department;
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

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public boolean isRaisesUpdateFlag() {
        return raisesUpdateFlag;
    }

    public void setRaisesUpdateFlag(boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public BigDecimal getOpportunityRatingAverage() {
        return opportunityRatingAverage;
    }

    public void setOpportunityRatingAverage(BigDecimal opportunityRatingAverage) {
        this.opportunityRatingAverage = opportunityRatingAverage;
    }

    public StateRepresentationSimple getPreviousState() {
        return previousState;
    }

    public void setPreviousState(StateRepresentationSimple previousState) {
        this.previousState = previousState;
    }

    public List<StateRepresentationSimple> getSecondaryStates() {
        return secondaryStates;
    }

    public void setSecondaryStates(List<StateRepresentationSimple> secondaryStates) {
        this.secondaryStates = secondaryStates;
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

    public List<PrismScopeRequiredSection> getAdvertIncompleteSections() {
        return advertIncompleteSections;
    }

    public void setAdvertIncompleteSections(List<PrismScopeRequiredSection> advertIncompleteSections) {
        this.advertIncompleteSections = advertIncompleteSections;
    }

    public void setParentResource(ResourceRepresentationSimple parentResource) {
        setProperty(this, parentResource.getScope().getLowerCamelName(), parentResource);
    }

}
