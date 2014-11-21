package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.math.BigDecimal;
import java.util.List;

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
}
