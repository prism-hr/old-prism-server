package uk.co.alumeni.prism.rest.representation.resource;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeSectionDefinition;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationStandard extends ResourceRepresentationRelation {

    private Boolean raisesUrgentFlag;

    private Integer readMessageCount = 0;

    private Integer unreadMessageCount = 0;

    private Boolean raisesUpdateFlag;

    private BigDecimal applicationRatingAverage;

    private StateRepresentationSimple previousState;

    private List<StateRepresentationSimple> secondaryStates;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    private List<PrismScopeSectionDefinition> advertIncompleteSections;

    private Integer stateActionPendingCount;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Integer getReadMessageCount() {
        return readMessageCount;
    }

    public void setReadMessageCount(Integer readMessageCount) {
        this.readMessageCount = readMessageCount;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public boolean getRaisesUpdateFlag() {
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

    public Integer getStateActionPendingCount() {
        return stateActionPendingCount;
    }

    public void setStateActionPendingCount(Integer stateActionPendingCount) {
        this.stateActionPendingCount = stateActionPendingCount;
    }

}
