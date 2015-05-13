package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ResourceSummaryPlotRepresentation {

    private Set<Set<ImportedEntity>> constraint;

    private ResourceSummaryPlotDataRepresentation data;

    public Set<Set<ImportedEntity>> getConstraint() {
        return constraint;
    }

    public ResourceSummaryPlotDataRepresentation getData() {
        return data;
    }

    public ResourceSummaryPlotRepresentation withConstraint(Set<Set<ImportedEntity>> constraint) {
        this.constraint = constraint;
        return this;
    }

    public ResourceSummaryPlotRepresentation withData(ResourceSummaryPlotDataRepresentation data) {
        this.data = data;
        return this;
    }

    public static class ResourceSummaryPlotDataRepresentation {

        private List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries;

        public List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> getProcessingSummaries() {
            return processingSummaries;
        }

        public void setProcessingSummaries(List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
            this.processingSummaries = processingSummaries;
        }

        public ResourceSummaryPlotDataRepresentation withProcessingSummaries(List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
            this.processingSummaries = processingSummaries;
            return this;
        }

        public static class ApplicationProcessingSummaryRepresentationYear extends ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation {

            private String applicationYear;

            private BigDecimal percentageComplete;

            private List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth> processingSummaries;

            public String getApplicationYear() {
                return applicationYear;
            }

            public void setApplicationYear(String applicationYear) {
                this.applicationYear = applicationYear;
            }

            public BigDecimal getPercentageComplete() {
                return percentageComplete;
            }

            public void setPercentageComplete(BigDecimal percentageComplete) {
                this.percentageComplete = percentageComplete;
            }

            public List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth> getProcessingSummaries() {
                return processingSummaries;
            }

            public void setProcessingSummaries(List<ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth> processingSummaries) {
                this.processingSummaries = processingSummaries;
            }

        }

        public static class ApplicationProcessingSummaryRepresentationMonth extends ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation {

            private Integer applicationMonth;

            public Integer getApplicationMonth() {
                return applicationMonth;
            }

            public void setApplicationMonth(Integer applicationMonth) {
                this.applicationMonth = applicationMonth;
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

}
