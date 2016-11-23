package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

public class ProfileListRowDTO implements ProfileEntityDTO {

    private Integer userId;

    private String userFirstName;

    private String userFirstName2;

    private String userFirstName3;

    private String userLastName;

    private String userEmail;

    private String userAccountImageUrl;

    private String linkedInProfileUrl;

    private Integer completeScore;

    private Long applicationCount;

    private Long applicationRatingCount;

    private Double applicationRatingAverage;

    private DateTime updatedTimestamp;

    private String sequenceIdentifier;

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

    public void setUserAccountImageUrl(String userAccountImageUrl) {
        this.userAccountImageUrl = userAccountImageUrl;
    }

    public String getLinkedInProfileUrl() {
        return linkedInProfileUrl;
    }

    public void setLinkedInProfileUrl(String linkedInProfileURL) {
        this.linkedInProfileUrl = linkedInProfileURL;
    }

    public Integer getCompleteScore() {
        return completeScore;
    }

    public void setCompleteScore(Integer completeScore) {
        this.completeScore = completeScore;
    }

    public Long getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Long getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Long applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public Double getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(Double applicationRatingAverage) {
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

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ProfileListRowDTO other = (ProfileListRowDTO) object;
        return Objects.equal(userId, other.getUserId());
    }

}
