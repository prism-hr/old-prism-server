package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class WorkflowPropertyConfigurationDTO extends ArrayList<WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO> {

    public static class WorkflowPropertyConfigurationValueDTO extends WorkflowConfigurationDTO {

        @NotNull
        private PrismWorkflowPropertyDefinition definitionId;

        @NotNull
        private Boolean enabled;

        @Min(0)
        @Max(999)
        private Integer minimum;

        @Min(0)
        @Max(999)
        private Integer maximum;

        public PrismWorkflowPropertyDefinition getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismWorkflowPropertyDefinition definitionId) {
            this.definitionId = definitionId;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMinimum() {
            return minimum;
        }

        public void setMinimum(Integer minimum) {
            this.minimum = minimum;
        }

        public Integer getMaximum() {
            return maximum;
        }

        public void setMaximum(Integer maximum) {
            this.maximum = maximum;
        }

        public WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO withId(PrismWorkflowPropertyDefinition id) {
            setDefinitionId(id);
            return this;
        }

        public WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO withMinimum(Integer minimum) {
            this.minimum = minimum;
            return this;
        }

        public WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO withMaximum(Integer maximum) {
            this.maximum = maximum;
            return this;
        }

    }

}
