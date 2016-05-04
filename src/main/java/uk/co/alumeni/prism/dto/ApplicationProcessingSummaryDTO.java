package uk.co.alumeni.prism.dto;

public class ApplicationProcessingSummaryDTO {

    private String applicationYear;

    private Integer applicationMonth;

    private Integer applicationWeek;

    private Long advertCount;

    private Long createdApplicationCount;

    private Long submittedApplicationCount;

    private Long approvedApplicationCount;

    private Long rejectedApplicationCount;

    private Long withdrawnApplicationCount;

    private Double createdApplicationRatio;

    private Double submittedApplicationRatio;

    private Double approvedApplicationRatio;

    private Double rejectedApplicationRatio;

    private Double withdrawnApplicationRatio;

    private Double averageRating;

    private Double averagePreparationTime;

    private Double averageProcessingTime;

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
    }

    public Integer getApplicationMonth() {
        return applicationMonth;
    }

    public void setApplicationMonth(Integer applicationMonth) {
        this.applicationMonth = applicationMonth;
    }

    public Integer getApplicationWeek() {
        return applicationWeek;
    }

    public void setApplicationWeek(Integer applicationWeek) {
        this.applicationWeek = applicationWeek;
    }

    public Long getAdvertCount() {
        return advertCount;
    }

    public void setAdvertCount(Long advertCount) {
        this.advertCount = advertCount;
    }

    public Long getCreatedApplicationCount() {
        return createdApplicationCount;
    }

    public void setCreatedApplicationCount(Long createdApplicationCount) {
        this.createdApplicationCount = createdApplicationCount;
    }

    public Long getSubmittedApplicationCount() {
        return submittedApplicationCount;
    }

    public void setSubmittedApplicationCount(Long submittedApplicationCount) {
        this.submittedApplicationCount = submittedApplicationCount;
    }

    public Long getApprovedApplicationCount() {
        return approvedApplicationCount;
    }

    public void setApprovedApplicationCount(Long approvedApplicationCount) {
        this.approvedApplicationCount = approvedApplicationCount;
    }

    public Long getRejectedApplicationCount() {
        return rejectedApplicationCount;
    }

    public void setRejectedApplicationCount(Long rejectedApplicationCount) {
        this.rejectedApplicationCount = rejectedApplicationCount;
    }

    public Long getWithdrawnApplicationCount() {
        return withdrawnApplicationCount;
    }

    public void setWithdrawnApplicationCount(Long withdrawnApplicationCount) {
        this.withdrawnApplicationCount = withdrawnApplicationCount;
    }

    public Double getCreatedApplicationRatio() {
        return createdApplicationRatio;
    }

    public void setCreatedApplicationRatio(Double createdApplicationRatio) {
        this.createdApplicationRatio = createdApplicationRatio;
    }

    public Double getSubmittedApplicationRatio() {
        return submittedApplicationRatio;
    }

    public void setSubmittedApplicationRatio(Double submittedApplicationRatio) {
        this.submittedApplicationRatio = submittedApplicationRatio;
    }

    public Double getApprovedApplicationRatio() {
        return approvedApplicationRatio;
    }

    public void setApprovedApplicationRatio(Double approvedApplicationRatio) {
        this.approvedApplicationRatio = approvedApplicationRatio;
    }

    public Double getRejectedApplicationRatio() {
        return rejectedApplicationRatio;
    }

    public void setRejectedApplicationRatio(Double rejectedApplicationRatio) {
        this.rejectedApplicationRatio = rejectedApplicationRatio;
    }

    public Double getWithdrawnApplicationRatio() {
        return withdrawnApplicationRatio;
    }

    public void setWithdrawnApplicationRatio(Double withdrawnApplicationRatio) {
        this.withdrawnApplicationRatio = withdrawnApplicationRatio;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Double getAveragePreparationTime() {
        return averagePreparationTime;
    }

    public void setAveragePreparationTime(Double averagePreparationTime) {
        this.averagePreparationTime = averagePreparationTime;
    }

    public Double getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public void setAverageProcessingTime(Double averageProcessingTime) {
        this.averageProcessingTime = averageProcessingTime;
    }

}
