package uk.co.alumeni.prism.rest.representation.profile;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ProfileListRowRepresentation extends ProfileRepresentationMessage {

    private boolean raisesUpdateFlag;

    private UserRepresentationSimple user;

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

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
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

    public ProfileListRowRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
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
