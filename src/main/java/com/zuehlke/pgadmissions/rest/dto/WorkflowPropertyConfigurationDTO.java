package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowProperty;

public class WorkflowPropertyConfigurationDTO {

    private List<WorkflowPropertyConfigurationValueDTO> values = Lists.newArrayList();

    public final List<WorkflowPropertyConfigurationValueDTO> getValues() {
        return values;
    }

    public final void setValues(List<WorkflowPropertyConfigurationValueDTO> values) {
        this.values = values;
    }

    public WorkflowPropertyConfigurationDTO addValue(WorkflowPropertyConfigurationValueDTO value) {
        values.add(value);
        return this;
    }

    public static class WorkflowPropertyConfigurationValueDTO {

        @NotNull
        private PrismWorkflowProperty definition;

        @NotNull
        private Boolean enabled;

        @Min(0)
        @Max(999)
        private Integer minimum;

        @Min(0)
        @Max(999)
        private Integer maximum;

        public final PrismWorkflowProperty getDefinition() {
            return definition;
        }

        public final void setDefinition(PrismWorkflowProperty definition) {
            this.definition = definition;
        }

        public final Boolean getEnabled() {
            return enabled;
        }

        public final void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public final Integer getMinimum() {
            return minimum;
        }

        public final void setMinimum(Integer minimum) {
            this.minimum = minimum;
        }

        public final Integer getMaximum() {
            return maximum;
        }

        public final void setMaximum(Integer maximum) {
            this.maximum = maximum;
        }

        public WorkflowPropertyConfigurationValueDTO withDefinition(PrismWorkflowProperty definition) {
            this.definition = definition;
            return this;
        }

        public WorkflowPropertyConfigurationValueDTO withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WorkflowPropertyConfigurationValueDTO withMinimum(Integer minimum) {
            this.minimum = minimum;
            return this;
        }

        public WorkflowPropertyConfigurationValueDTO withMaximum(Integer maximum) {
            this.maximum = maximum;
            return this;
        }

    }

}
