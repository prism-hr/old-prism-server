package com.zuehlke.pgadmissions.rest.representation;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceSummaryRepresentation {

    private LocalDate createdDate;

    private Integer programCount;

    private Integer projectCount;

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

        private BigDecimal instanceOccurenceAverage;

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

        public final BigDecimal getInstanceOccurenceAverage() {
            return instanceOccurenceAverage;
        }

        public final void setInstanceOccurenceAverage(BigDecimal instanceOccurenceAverage) {
            this.instanceOccurenceAverage = instanceOccurenceAverage;
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

        public ApplicationProcessingSummaryRepresentation withInstanceOccurenceAverage(BigDecimal instanceOccurenceAverage) {
            this.instanceOccurenceAverage = instanceOccurenceAverage;
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

}
