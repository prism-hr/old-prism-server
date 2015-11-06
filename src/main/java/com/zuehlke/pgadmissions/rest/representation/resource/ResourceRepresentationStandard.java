package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationStandard extends ResourceRepresentationRelation {

    private boolean raisesUrgentFlag;

    private boolean raisesUpdateFlag;

    private BigDecimal applicationRatingAverage;

    private StateRepresentationSimple previousState;

    private List<StateRepresentationSimple> secondaryStates;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    private List<PrismScopeSectionDefinition> advertIncompleteSections;

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

    public List<PrismScopeSectionDefinition> getAdvertIncompleteSections() {
        return advertIncompleteSections;
    }

    public void setAdvertIncompleteSections(List<PrismScopeSectionDefinition> advertIncompleteSections) {
        this.advertIncompleteSections = advertIncompleteSections;
    }

}
