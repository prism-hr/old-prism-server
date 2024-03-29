package uk.co.alumeni.prism.rest.representation.profile;

import org.joda.time.DateTime;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import java.math.BigDecimal;
import java.util.List;

public class ProfileListRowRepresentation extends ProfileRepresentationMessage {

    private boolean raisesUpdateFlag;

    private BigDecimal completeScore;

    private UserRepresentationSimple user;

    private List<String> locations;

    private List<ResourceRepresentationRelation> organizations;

    private String linkedInProfileUrl;

    private Integer applicationCount;

    private Integer applicationRatingCount;

    private BigDecimal applicationRatingAverage;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

    public boolean isRaisesUpdateFlag() {
        return raisesUpdateFlag;
    }

    public void setRaisesUpdateFlag(boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
    }

    public BigDecimal getCompleteScore() {
        return completeScore;
    }

    public void setCompleteScore(BigDecimal completeScore) {
        this.completeScore = completeScore;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<ResourceRepresentationRelation> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<ResourceRepresentationRelation> organizations) {
        this.organizations = organizations;
    }

    public String getLinkedInProfileUrl() {
        return linkedInProfileUrl;
    }

    public void setLinkedInProfileUrl(String linkedInProfileUrl) {
        this.linkedInProfileUrl = linkedInProfileUrl;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
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

    public ProfileListRowRepresentation withReadMessageCount(Integer readMessageCount) {
        setReadMessageCount(readMessageCount);
        return this;
    }

    public ProfileListRowRepresentation withUnreadMessageCount(Integer unreadMessageCount) {
        setUnreadMessageCount(unreadMessageCount);
        return this;
    }

    public ProfileListRowRepresentation withRaisesUpdateFlag(boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
        return this;
    }

    public ProfileListRowRepresentation withCompleteScore(BigDecimal completeScore) {
        this.completeScore = completeScore;
        return this;
    }

    public ProfileListRowRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ProfileListRowRepresentation withLocations(List<String> locations) {
        this.locations = locations;
        return this;
    }

    public ProfileListRowRepresentation withOrganizations(List<ResourceRepresentationRelation> organizations) {
        this.organizations = organizations;
        return this;
    }

    public ProfileListRowRepresentation withLinkedInProfileUrl(String linkedInProfileUrl) {
        this.linkedInProfileUrl = linkedInProfileUrl;
        return this;
    }

    public ProfileListRowRepresentation withApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
        return this;
    }

    public ProfileListRowRepresentation withApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
        return this;
    }

    public ProfileListRowRepresentation withApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
        return this;
    }

    public ProfileListRowRepresentation withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    public ProfileListRowRepresentation withSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
        return this;
    }

}
