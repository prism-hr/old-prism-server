package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.rest.dto.ActionCustomQuestionConfigurationDTO.ActionCustomQuestionConfigurationValueDTO;

public class ActionCustomQuestionConfigurationDTO extends ArrayList<ActionCustomQuestionConfigurationValueDTO> {

    private static final long serialVersionUID = 3104422819748158358L;

    public static class ActionCustomQuestionConfigurationValueDTO extends WorkflowConfigurationDTO {

        @NotNull
        private PrismActionCustomQuestionDefinition definitionId;

        @NotEmpty
        private String component;

        @NotNull
        private Boolean editable;

        @NotNull
        private Integer index;

        @NotEmpty
        private String label;

        @NotEmpty
        private String description;

        @Size(min = 1)
        private String placeholder;

        @Size(min = 1)
        private List<String> options;

        @NotNull
        private Boolean required;

        private String validation;

        @DecimalMin("0.01")
        @DecimalMax("1.00")
        private BigDecimal weighting;

        public PrismActionCustomQuestionDefinition getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(PrismActionCustomQuestionDefinition definitionId) {
            this.definitionId = definitionId;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public Boolean getEditable() {
            return editable;
        }

        public void setEditable(Boolean editable) {
            this.editable = editable;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public String getValidation() {
            return validation;
        }

        public void setValidation(String validation) {
            this.validation = validation;
        }

        public BigDecimal getWeighting() {
            return weighting;
        }

        public void setWeighting(BigDecimal weighting) {
            this.weighting = weighting;
        }

    }

}
