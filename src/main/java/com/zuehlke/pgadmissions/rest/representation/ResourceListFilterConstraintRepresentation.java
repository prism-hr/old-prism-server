package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

public class ResourceListFilterConstraintRepresentation {

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

    public final DateTime getValueDateTimeStart() {
        return valueDateStart.toDateTimeAtStartOfDay();
    }

    public final DateTime getValueDateTimeClose() {
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

    public ResourceListFilterConstraintRepresentation withFilterProperty(FilterProperty filterProperty) {
        this.filterProperty = filterProperty;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withFilterExpression(FilterExpression filterExpression) {
        this.filterExpression = filterExpression;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withNegated(Boolean negated) {
        this.negated = negated;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueStateGroup(PrismStateGroup valueStateGroup) {
        this.valueStateGroup = valueStateGroup;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueDateStart(LocalDate valueDateStart) {
        this.valueDateStart = valueDateStart;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueDateClose(LocalDate valueDateClose) {
        this.valueDateClose = valueDateClose;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueDecimalStart(BigDecimal valueDecimalStart) {
        this.valueDecimalStart = valueDecimalStart;
        return this;
    }

    public ResourceListFilterConstraintRepresentation withValueDecimalClose(BigDecimal valueDecimalClose) {
        this.valueDecimalClose = valueDecimalClose;
        return this;
    }

    public void addValueRole(PrismRole role) {
        valueRoles.add(role);
    }

}
