package com.zuehlke.pgadmissions.rest.dto.resource;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListContraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ResourceListFilterConstraintDTO {

    private PrismResourceListContraint filterProperty;

    private PrismResourceListFilterExpression filterExpression;

    private Boolean negated;

    private Integer displayPosition;

    private String valueString;

    private LocalDate valueDateStart;

    private LocalDate valueDateClose;

    private PrismStateGroup valueStateGroup;

    private PrismApplicationReserveStatus valueReserveStatus;

    private BigDecimal valueDecimalStart;

    private BigDecimal valueDecimalClose;

    public PrismResourceListContraint getFilterProperty() {
        return filterProperty;
    }

    public void setFilterProperty(PrismResourceListContraint filterProperty) {
        this.filterProperty = filterProperty;
    }

    public PrismResourceListFilterExpression getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(PrismResourceListFilterExpression filterExpression) {
        this.filterExpression = filterExpression;
    }

    public Boolean getNegated() {
        return negated;
    }

    public void setNegated(Boolean negated) {
        this.negated = negated;
    }

    public Integer getDisplayPosition() {
        return displayPosition;
    }

    public void setDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public LocalDate getValueDateStart() {
        return valueDateStart;
    }

    public void setValueDateStart(LocalDate valueDateStart) {
        this.valueDateStart = valueDateStart;
    }

    public LocalDate getValueDateClose() {
        return valueDateClose;
    }

    public void setValueDateClose(LocalDate valueDateClose) {
        this.valueDateClose = valueDateClose;
    }

    public PrismStateGroup getValueStateGroup() {
        return valueStateGroup;
    }

    public PrismApplicationReserveStatus getValueReserveStatus() {
        return valueReserveStatus;
    }

    public void setValueReserveStatus(PrismApplicationReserveStatus valueReserveStatus) {
        this.valueReserveStatus = valueReserveStatus;
    }

    public DateTime computeValueDateTimeStart() {
        return valueDateStart == null ? null : valueDateStart.toDateTimeAtStartOfDay();
    }

    public DateTime computeValueDateTimeClose() {
        return valueDateClose == null ? null :  valueDateClose.plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1);
    }

    public void setValueStateGroup(PrismStateGroup valueStateGroup) {
        this.valueStateGroup = valueStateGroup;
    }

    public BigDecimal getValueDecimalStart() {
        return valueDecimalStart;
    }

    public void setValueDecimalStart(BigDecimal valueDecimalStart) {
        this.valueDecimalStart = valueDecimalStart;
    }

    public BigDecimal getValueDecimalClose() {
        return valueDecimalClose;
    }

    public void setValueDecimalClose(BigDecimal valueDecimalClose) {
        this.valueDecimalClose = valueDecimalClose;
    }

    public ResourceListFilterConstraintDTO withFilterProperty(PrismResourceListContraint filterProperty) {
        this.filterProperty = filterProperty;
        return this;
    }

    public ResourceListFilterConstraintDTO withFilterExpression(PrismResourceListFilterExpression filterExpression) {
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

    public Object[] getValues() {
        Collection<Object> filterValues = Lists.<Object>newArrayList(valueString, valueDateStart, valueDateClose, valueStateGroup, valueReserveStatus,
                valueDecimalStart, valueDecimalClose);
        return (Object[]) Collections2.filter(filterValues, Predicates.notNull()).toArray();
    }

}
