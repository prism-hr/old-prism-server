package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.List;

public class ResourceSummaryPlotDataRepresentation {

    private List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries;

    public List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> getProcessingSummaries() {
        return processingSummaries;
    }

    public void setProcessingSummaries(List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
        this.processingSummaries = processingSummaries;
    }

    public ResourceSummaryPlotDataRepresentation withProcessingSummaries(
            List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
        this.processingSummaries = processingSummaries;
        return this;
    }

    public static class ApplicationProcessingSummaryRepresentationYear extends ApplicationProcessingSummaryRepresentation {

        private String applicationYear;

        private List<ApplicationProcessingSummaryRepresentationMonth> processingSummaries;

        public String getApplicationYear() {
            return applicationYear;
        }

        public void setApplicationYear(String applicationYear) {
            this.applicationYear = applicationYear;
        }

        public List<ApplicationProcessingSummaryRepresentationMonth> getProcessingSummaries() {
            return processingSummaries;
        }

        public void setProcessingSummaries(List<ApplicationProcessingSummaryRepresentationMonth> processingSummaries) {
            this.processingSummaries = processingSummaries;
        }

    }

    public static class ApplicationProcessingSummaryRepresentationMonth extends ApplicationProcessingSummaryRepresentation {

        private Integer applicationMonth;

        private List<ApplicationProcessingSummaryRepresentationWeek> processingSummaries;

        public Integer getApplicationMonth() {
            return applicationMonth;
        }

        public void setApplicationMonth(Integer applicationMonth) {
            this.applicationMonth = applicationMonth;
        }

        public List<ApplicationProcessingSummaryRepresentationWeek> getProcessingSummaries() {
            return processingSummaries;
        }

        public void setProcessingSummaries(List<ApplicationProcessingSummaryRepresentationWeek> processingSummaries) {
            this.processingSummaries = processingSummaries;
        }

    }

    public static class ApplicationProcessingSummaryRepresentationWeek extends ApplicationProcessingSummaryRepresentation {

        private Integer applicationWeek;

        public Integer getApplicationWeek() {
            return applicationWeek;
        }

        public void setApplicationWeek(Integer applicationWeek) {
            this.applicationWeek = applicationWeek;
        }

    }

    public static class ApplicationProcessingSummaryRepresentation {

        private Integer advertCount;

        private Integer submittedApplicationCount;

        private Integer approvedApplicationCount;

        private Integer rejectedApplicationCount;

        private Integer withdrawnApplicationCount;

        private BigDecimal submittedApplicationRatio;

        private BigDecimal approvedApplicationRatio;

        private BigDecimal rejectedApplicationRatio;

        private BigDecimal withdrawnApplicationRatio;

        private BigDecimal averageRating;

        private BigDecimal averageProcessingTime;

        public Integer getAdvertCount() {
            return advertCount;
        }

        public void setAdvertCount(Integer advertCount) {
            this.advertCount = advertCount;
        }

        public Integer getSubmittedApplicationCount() {
            return submittedApplicationCount;
        }

        public void setSubmittedApplicationCount(Integer submittedApplicationCount) {
            this.submittedApplicationCount = submittedApplicationCount;
        }

        public Integer getApprovedApplicationCount() {
            return approvedApplicationCount;
        }

        public void setApprovedApplicationCount(Integer approvedApplicationCount) {
            this.approvedApplicationCount = approvedApplicationCount;
        }

        public Integer getRejectedApplicationCount() {
            return rejectedApplicationCount;
        }

        public void setRejectedApplicationCount(Integer rejectedApplicationCount) {
            this.rejectedApplicationCount = rejectedApplicationCount;
        }

        public Integer getWithdrawnApplicationCount() {
            return withdrawnApplicationCount;
        }

        public void setWithdrawnApplicationCount(Integer withdrawnApplicationCount) {
            this.withdrawnApplicationCount = withdrawnApplicationCount;
        }

        public BigDecimal getSubmittedApplicationRatio() {
            return submittedApplicationRatio;
        }

        public void setSubmittedApplicationRatio(BigDecimal submittedApplicationRatio) {
            this.submittedApplicationRatio = submittedApplicationRatio;
        }

        public BigDecimal getApprovedApplicationRatio() {
            return approvedApplicationRatio;
        }

        public void setApprovedApplicationRatio(BigDecimal approvedApplicationRatio) {
            this.approvedApplicationRatio = approvedApplicationRatio;
        }

        public BigDecimal getRejectedApplicationRatio() {
            return rejectedApplicationRatio;
        }

        public void setRejectedApplicationRatio(BigDecimal rejectedApplicationRatio) {
            this.rejectedApplicationRatio = rejectedApplicationRatio;
        }

        public BigDecimal getWithdrawnApplicationRatio() {
            return withdrawnApplicationRatio;
        }

        public void setWithdrawnApplicationRatio(BigDecimal withdrawnApplicationRatio) {
            this.withdrawnApplicationRatio = withdrawnApplicationRatio;
        }

        public BigDecimal getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(BigDecimal averageRating) {
            this.averageRating = averageRating;
        }

        public BigDecimal getAverageProcessingTime() {
            return averageProcessingTime;
        }

        public void setAverageProcessingTime(BigDecimal averageProcessingTime) {
            this.averageProcessingTime = averageProcessingTime;
        }

    }

}