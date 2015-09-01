package com.zuehlke.pgadmissions.rest.dto;

import java.util.ArrayList;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.rest.dto.WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO;

public class WorkflowPropertyConfigurationDTO extends ArrayList<WorkflowPropertyConfigurationValueDTO> {

    private static final long serialVersionUID = 444009817902242462L;

    public static class WorkflowPropertyConfigurationValueDTO extends WorkflowConfigurationDTO {

        @NotNull
        private PrismWorkflowPropertyDefinition definitionId;

        private Boolean enabled;

        private Boolean required;

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

        public final Boolean getRequired() {
            return required;
        }

        public final void setRequired(Boolean required) {
            this.required = required;
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

        public WorkflowPropertyConfigurationValueDTO withDefinition(PrismWorkflowPropertyDefinition definitionId) {
            setDefinitionId(definitionId);
            return this;
        }

        public WorkflowPropertyConfigurationValueDTO withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WorkflowPropertyConfigurationDTO.WorkflowPropertyConfigurationValueDTO withRequired(Boolean required) {
            this.required = required;
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
