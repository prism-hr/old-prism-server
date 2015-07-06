package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.math.BigDecimal;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;

public class ActionCustomQuestionConfigurationRepresentation extends WorkflowConfigurationVersionedRepresentation {

    private Integer id;

    private String component;

    private Boolean editable;

    private Integer index;

    private String label;

    private String description;

    private String placeholder;

    private List<String> options;

    private Boolean required;

    private String validation;

    private BigDecimal weighting;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ActionCustomQuestionConfigurationRepresentation withProperty(PrismActionCustomQuestionDefinition property) {
        setProperty(property);
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withComponent(String component) {
        this.component = component;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withEditable(Boolean editable) {
        this.editable = editable;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withIndex(Integer index) {
        this.index = index;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withLabel(String label) {
        this.label = label;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withOptions(List<String> options) {
        this.options = options;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withRequired(Boolean required) {
        this.required = required;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withValidation(String validation) {
        this.validation = validation;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withWeighting(BigDecimal weighting) {
        this.weighting = weighting;
        return this;
    }
    
    public ActionCustomQuestionConfigurationRepresentation withVersion(Integer version) {
        setVersion(version);
        return this;
    }
    
}
