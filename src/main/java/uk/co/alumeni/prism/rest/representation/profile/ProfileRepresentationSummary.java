package uk.co.alumeni.prism.rest.representation.profile;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentationRatingSummary;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSummary;

import java.math.BigDecimal;
import java.util.List;

public class ProfileRepresentationSummary extends ResourceRepresentationSummary {

    private Integer applicationCount;

    private List<CommentRepresentationRatingSummary> actionSummaries;

    private List<ProfileQualificationRepresentation> recentQualifications;

    private List<ProfileEmploymentPositionRepresentation> recentEmploymentPositions;

    private DocumentRepresentation cv;

    private String personalSummary;

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Integer getApplicationRatingCount() {
        return null;
    }

    public void setApplicationRatingCount(Integer applicationRatingCount) {
    }

    public BigDecimal getApplicationRatingAverage() {
        return null;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
    }

    public List<CommentRepresentationRatingSummary> getActionSummaries() {
        return actionSummaries;
    }

    public void setActionSummaries(List<CommentRepresentationRatingSummary> actionSummaries) {
        this.actionSummaries = actionSummaries;
    }

    public List<ProfileQualificationRepresentation> getRecentQualifications() {
        return recentQualifications;
    }

    public void setRecentQualifications(List<ProfileQualificationRepresentation> recentQualifications) {
        this.recentQualifications = recentQualifications;
    }

    public List<ProfileEmploymentPositionRepresentation> getRecentEmploymentPositions() {
        return recentEmploymentPositions;
    }

    public void setRecentEmploymentPositions(List<ProfileEmploymentPositionRepresentation> recentEmploymentPositions) {
        this.recentEmploymentPositions = recentEmploymentPositions;
    }

    public DocumentRepresentation getCv() {
        return cv;
    }

    public void setCv(DocumentRepresentation cv) {
        this.cv = cv;
    }

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

}
