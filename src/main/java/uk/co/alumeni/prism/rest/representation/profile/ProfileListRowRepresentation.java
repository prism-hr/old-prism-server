package uk.co.alumeni.prism.rest.representation.profile;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ProfileListRowRepresentation {

    private boolean raisesUpdateFlag;

    private UserRepresentationSimple user;

    private String personalSummary;

    private DocumentRepresentation cv;

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

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    public DocumentRepresentation getCv() {
        return cv;
    }

    public void setCv(DocumentRepresentation cv) {
        this.cv = cv;
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

    public ProfileListRowRepresentation withRaisesUpdateFlag(boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
        return this;
    }

    public ProfileListRowRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ProfileListRowRepresentation withPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
        return this;
    }

    public ProfileListRowRepresentation withCv(DocumentRepresentation cv) {
        this.cv = cv;
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
