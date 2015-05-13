package com.zuehlke.pgadmissions.rest.representation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;

public class ResourceSummaryRepresentation {
    
    private List<ResourceSummaryPlotRepresentation> plots = Lists.newLinkedList();

    public List<ResourceSummaryPlotRepresentation> getPlots() {
        return plots;
    }

    public ResourceSummaryRepresentation addPlot(ResourceSummaryPlotRepresentation plot) {
        plots.add(plot);
        return this;
    }

    public static class ResourceSummaryPlotRepresentation {

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

            private LocalDate createdDate;

            private Integer programCount;

            private Integer projectCount;

            private List<ApplicationProcessingSummaryRepresentationYear> processingSummaries;

            public final LocalDate getCreatedDate() {
                return createdDate;
            }

            public final void setCreatedDate(LocalDate createdDate) {
                this.createdDate = createdDate;
            }

            public final Integer getProgramCount() {
                return programCount;
            }

            public final void setProgramCount(Integer programCount) {
                this.programCount = programCount;
            }

            public final Integer getProjectCount() {
                return projectCount;
            }

            public final void setProjectCount(Integer projectCount) {
                this.projectCount = projectCount;
            }

            public List<ApplicationProcessingSummaryRepresentationYear> getProcessingSummaries() {
                return processingSummaries;
            }

            public void setProcessingSummaries(List<ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
                this.processingSummaries = processingSummaries;
            }

            public ResourceSummaryPlotDataRepresentation withCreatedDate(LocalDate createdDate) {
                this.createdDate = createdDate;
                return this;
            }

            public ResourceSummaryPlotDataRepresentation withProgramCount(Integer programCount) {
                this.programCount = programCount;
                return this;
            }

            public ResourceSummaryPlotDataRepresentation withProjectCount(Integer projectCount) {
                this.projectCount = projectCount;
                return this;
            }

            public ResourceSummaryPlotDataRepresentation withProcessingSummaries(List<ApplicationProcessingSummaryRepresentationYear> processingSummaries) {
                this.processingSummaries = processingSummaries;
                return this;
            }

            public static class ApplicationProcessingSummaryRepresentationYear extends ApplicationProcessingSummaryRepresentation {

                private String applicationYear;

                private BigDecimal percentageComplete;

                private List<ApplicationProcessingSummaryRepresentationMonth> processingSummaries;

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

                public List<ApplicationProcessingSummaryRepresentationMonth> getProcessingSummaries() {
                    return processingSummaries;
                }

                public void setProcessingSummaries(List<ApplicationProcessingSummaryRepresentationMonth> processingSummaries) {
                    this.processingSummaries = processingSummaries;
                }

            }

            public static class ApplicationProcessingSummaryRepresentationMonth extends ApplicationProcessingSummaryRepresentation {

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

                private BigDecimal averagePreparationTime;

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

                public BigDecimal getAveragePreparationTime() {
                    return averagePreparationTime;
                }

                public void setAveragePreparationTime(BigDecimal averagePreparationTime) {
                    this.averagePreparationTime = averagePreparationTime;
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

}
