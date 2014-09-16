package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceListFilterConstraintDTO {

    private FilterProperty filterProperty;

    private FilterExpression filterExpression;

    private Boolean negated;

    private Integer displayPosition;

    private String valueString;

    private LocalDate valueDateStart;

    private LocalDate valueDateClose;

    private PrismStateGroup valueStateGroup;

    private BigDecimal valueDecimalStart;

    private BigDecimal valueDecimalClose;

    private List<PrismRole> valueRoles;

    public final FilterProperty getFilterProperty() {
        return filterProperty;
    }

    public final void setFilterProperty(FilterProperty filterProperty) {
        this.filterProperty = filterProperty;
    }

    public final FilterExpression getFilterExpression() {
        return filterExpression;
    }

    public final void setFilterExpression(FilterExpression filterExpression) {
        this.filterExpression = filterExpression;
    }

    public final Boolean getNegated() {
        return negated;
    }

    public final void setNegated(Boolean negated) {
        this.negated = negated;
    }

    public final Integer getDisplayPosition() {
        return displayPosition;
    }

    public final void setDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
    }

    public final String getValueString() {
        return valueString;
    }

    public final void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public final LocalDate getValueDateStart() {
        return valueDateStart;
    }

    public final void setValueDateStart(LocalDate valueDateStart) {
        this.valueDateStart = valueDateStart;
    }

    public final LocalDate getValueDateClose() {
        return valueDateClose;
    }

    public final void setValueDateClose(LocalDate valueDateClose) {
        this.valueDateClose = valueDateClose;
    }

    public final PrismStateGroup getValueStateGroup() {
        return valueStateGroup;
    }

    public final DateTime computeValueDateTimeStart() {
        return valueDateStart.toDateTimeAtStartOfDay();
    }

    public final DateTime computeValueDateTimeClose() {
        return valueDateClose.plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1);
    }

    public final void setValueStateGroup(PrismStateGroup valueStateGroup) {
        this.valueStateGroup = valueStateGroup;
    }

    public final BigDecimal getValueDecimalStart() {
        return valueDecimalStart;
    }

    public final void setValueDecimalStart(BigDecimal valueDecimalStart) {
        this.valueDecimalStart = valueDecimalStart;
    }

    public final BigDecimal getValueDecimalClose() {
        return valueDecimalClose;
    }

    public final void setValueDecimalClose(BigDecimal valueDecimalClose) {
        this.valueDecimalClose = valueDecimalClose;
    }

    public final List<PrismRole> getValueRoles() {
        return valueRoles;
    }

    public final void setValueRoles(List<PrismRole> valueRoles) {
        this.valueRoles = valueRoles;
    }

    public ResourceListFilterConstraintDTO withFilterProperty(FilterProperty filterProperty) {
        this.filterProperty = filterProperty;
        return this;
    }

    public ResourceListFilterConstraintDTO withFilterExpression(FilterExpression filterExpression) {
        this.filterExpression = filterExpression;
        return this;
    }

    public ResourceListFilterConstraintDTO withNegated(Boolean negated) {
        this.negated = negated;
        return this;
    }

    public ResourceListFilterConstraintDTO withDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueStateGroup(PrismStateGroup valueStateGroup) {
        this.valueStateGroup = valueStateGroup;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueDateStart(LocalDate valueDateStart) {
        this.valueDateStart = valueDateStart;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueDateClose(LocalDate valueDateClose) {
        this.valueDateClose = valueDateClose;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueDecimalStart(BigDecimal valueDecimalStart) {
        this.valueDecimalStart = valueDecimalStart;
        return this;
    }

    public ResourceListFilterConstraintDTO withValueDecimalClose(BigDecimal valueDecimalClose) {
        this.valueDecimalClose = valueDecimalClose;
        return this;
    }

    public void addValueRole(PrismRole role) {
        valueRoles.add(role);
    }

}
