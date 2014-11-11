package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.Sets;

public class ActionPropertyConfigurationDTO {

    @NotNull
    private String name;

    @NotNull
    private Boolean editable;

    @NotNull
    private Integer index;

    @NotNull
    private String label;

    @NotNull
    private String description;

    @NotEmpty
    private String placeholder;

    @NotBlank
    private Set<String> options = Sets.newLinkedHashSet();

    @NotNull
    private Boolean required;

    @NotBlank
    private Set<String> validationRules = Sets.newLinkedHashSet();

    @DecimalMin("0.01")
    @DecimalMax("1.00")
    private BigDecimal weighting;

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final Boolean getEditable() {
        return editable;
    }

    public final void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public final Integer getIndex() {
        return index;
    }

    public final void setIndex(Integer index) {
        this.index = index;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getPlaceholder() {
        return placeholder;
    }

    public final void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public final Set<String> getOptions() {
        return options;
    }

    public final void setOptions(Set<String> options) {
        this.options = options;
    }

    public final Boolean getRequired() {
        return required;
    }

    public final void setRequired(Boolean required) {
        this.required = required;
    }

    public final Set<String> getValidationRules() {
        return validationRules;
    }

    public final void setValidationRules(Set<String> validationRules) {
        this.validationRules = validationRules;
    }

    public final BigDecimal getWeighting() {
        return weighting;
    }

    public final void setWeighting(BigDecimal weighting) {
        this.weighting = weighting;
    }

}
