package com.zuehlke.pgadmissions.rest.representation;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceSummaryRepresentation {

    private LocalDate createdDate;

    private Integer programCount;

    private Integer projectCount;

    private Integer applicationCreatedCount;

    private Integer applicationSubmittedCount;

    private Integer applicationApprovedCount;

    private Integer applicationRejectedCount;

    private Integer applicationWithdrawnCount;

    private Integer applicationRatingCount;

    private BigDecimal applicationRatingOccurenceAverage;

    private List<ApplicationProcessingSummaryRepresentation> processingSummaries;

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

    public final Integer getApplicationCreatedCount() {
        return applicationCreatedCount;
    }

    public final void setApplicationCreatedCount(Integer applicationCreatedCount) {
        this.applicationCreatedCount = applicationCreatedCount;
    }

    public final Integer getApplicationSubmittedCount() {
        return applicationSubmittedCount;
    }

    public final void setApplicationSubmittedCount(Integer applicationSubmittedCount) {
        this.applicationSubmittedCount = applicationSubmittedCount;
    }

    public final Integer getApplicationApprovedCount() {
        return applicationApprovedCount;
    }

    public final void setApplicationApprovedCount(Integer applicationApprovedCount) {
        this.applicationApprovedCount = applicationApprovedCount;
    }

    public final Integer getApplicationRejectedCount() {
        return applicationRejectedCount;
    }

    public final void setApplicationRejectedCount(Integer applicationRejectedCount) {
        this.applicationRejectedCount = applicationRejectedCount;
    }

    public final Integer getApplicationWithdrawnCount() {
        return applicationWithdrawnCount;
    }

    public final void setApplicationWithdrawnCount(Integer applicationWithdrawnCount) {
        this.applicationWithdrawnCount = applicationWithdrawnCount;
    }

    public final Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public final void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public final BigDecimal getApplicationRatingOccurenceAverage() {
        return applicationRatingOccurenceAverage;
    }

    public final void setApplicationRatingOccurenceAverage(BigDecimal applicationRatingOccurenceAverage) {
        this.applicationRatingOccurenceAverage = applicationRatingOccurenceAverage;
    }

    public final List<ApplicationProcessingSummaryRepresentation> getProcessingSummaries() {
        return processingSummaries;
    }

    public final void setProcessingSummaries(List<ApplicationProcessingSummaryRepresentation> processingSummaries) {
        this.processingSummaries = processingSummaries;
    }

    public static class ApplicationProcessingSummaryRepresentation {

        private PrismStateGroup stateGroup;

        private Integer instanceTotal;

        private Integer instanceTotalLive;

        private BigDecimal instanceOccurrenceAverage;

        private BigDecimal instanceDurationAverage;

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public final Integer getInstanceTotal() {
            return instanceTotal;
        }

        public final void setInstanceTotal(Integer instanceTotal) {
            this.instanceTotal = instanceTotal;
        }

        public final Integer getInstanceTotalLive() {
            return instanceTotalLive;
        }

        public final void setInstanceTotalLive(Integer instanceTotalLive) {
            this.instanceTotalLive = instanceTotalLive;
        }

        public final BigDecimal getInstanceOccurrenceAverage() {
            return instanceOccurrenceAverage;
        }

        public final void setInstanceOccurrenceAverage(BigDecimal instanceOccurrenceAverage) {
            this.instanceOccurrenceAverage = instanceOccurrenceAverage;
        }

        public final BigDecimal getInstanceDurationAverage() {
            return instanceDurationAverage;
        }

        public final void setInstanceDurationAverage(BigDecimal instanceDurationAverage) {
            this.instanceDurationAverage = instanceDurationAverage;
        }

        public ApplicationProcessingSummaryRepresentation withStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
            return this;
        }

        public ApplicationProcessingSummaryRepresentation withInstanceTotal(Integer instanceTotal) {
            this.instanceTotal = instanceTotal;
            return this;
        }

        public ApplicationProcessingSummaryRepresentation withInstanceTotalLive(Integer instanceTotalLive) {
            this.instanceTotalLive = instanceTotalLive;
            return this;
        }

        public ApplicationProcessingSummaryRepresentation withInstanceOccurrenceAverage(BigDecimal instanceOccurrenceAverage) {
            this.instanceOccurrenceAverage = instanceOccurrenceAverage;
            return this;
        }

        public ApplicationProcessingSummaryRepresentation withInstanceDurationAverage(BigDecimal instanceDurationAverage) {
            this.instanceDurationAverage = instanceDurationAverage;
            return this;
        }

    }

    public ResourceSummaryRepresentation withCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public ResourceSummaryRepresentation withProgramCount(Integer programCount) {
        this.programCount = programCount;
        return this;
    }

    public ResourceSummaryRepresentation withProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationCreatedCount(Integer applicationCreatedCount) {
        this.applicationCreatedCount = applicationCreatedCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationSubmittedCount(Integer applicationSubmittedCount) {
        this.applicationSubmittedCount = applicationSubmittedCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationApprovedCount(Integer applicationApprovedCount) {
        this.applicationApprovedCount = applicationApprovedCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationRejectedCount(Integer applicationRejectedCount) {
        this.applicationRejectedCount = applicationRejectedCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationWithdrawnCount(Integer applicationWithdrawnCount) {
        this.applicationWithdrawnCount = applicationWithdrawnCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
        return this;
    }

    public ResourceSummaryRepresentation withApplicationRatingOccurenceAverage(BigDecimal applicationRatingOccurenceAverage) {
        this.applicationRatingOccurenceAverage = applicationRatingOccurenceAverage;
        return this;
    }

}
