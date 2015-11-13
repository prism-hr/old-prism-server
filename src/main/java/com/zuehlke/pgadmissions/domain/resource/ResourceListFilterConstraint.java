package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;

@Entity
@Table(name = "resource_list_filter_constraint", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "resource_list_filter_id", "filter_property", "filter_expression", "value_string" }),
        @UniqueConstraint(columnNames = { "resource_list_filter_id", "filter_property", "filter_expression", "value_date_start", "value_date_close" }),
        @UniqueConstraint(columnNames = { "resource_list_filter_id", "filter_property", "filter_expression", "value_state_group_id" }),
        @UniqueConstraint(columnNames = { "resource_list_filter_id", "filter_property", "filter_expression", "value_decimal_start", "value_decimal_close" }) })
public class ResourceListFilterConstraint {

    @Id
    @GeneratedValue
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "resource_list_filter_id", updatable = false, insertable = false)
    private ResourceListFilter filter;

    @Column(name = "filter_property", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismResourceListConstraint filterProperty;

    @Column(name = "filter_expression", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismResourceListFilterExpression filterExpression;

    @Column(name = "negated", nullable = false)
    private Boolean negated;

    @Column(name = "display_position", nullable = false)
    private Integer displayPosition;

    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_date_start")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate valueDateStart;

    @Column(name = "value_date_close")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate valueDateClose;

    @ManyToOne
    @JoinColumn(name = "value_state_group_id")
    private StateGroup valueStateGroup;

    @Column(name = "value_decimal_start")
    private BigDecimal valueDecimalStart;

    @Column(name = "value_decimal_close")
    private BigDecimal valueDecimalClose;

    public final Integer getId() {
        return Id;
    }

    public final void setId(Integer id) {
        Id = id;
    }

    public final ResourceListFilter getFilter() {
        return filter;
    }

    public final void setFilter(ResourceListFilter filter) {
        this.filter = filter;
    }

    public final PrismResourceListConstraint getFilterProperty() {
        return filterProperty;
    }

    public final void setFilterProperty(PrismResourceListConstraint filterProperty) {
        this.filterProperty = filterProperty;
    }

    public final PrismResourceListFilterExpression getFilterExpression() {
        return filterExpression;
    }

    public final void setFilterExpression(PrismResourceListFilterExpression filterExpression) {
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

    public final StateGroup getValueStateGroup() {
        return valueStateGroup;
    }

    public final void setValueStateGroup(StateGroup valueStateGroup) {
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

    public ResourceListFilterConstraint withFilter(ResourceListFilter filter) {
        this.filter = filter;
        return this;
    }

    public ResourceListFilterConstraint withFilterProperty(PrismResourceListConstraint filterProperty) {
        this.filterProperty = filterProperty;
        return this;
    }

    public ResourceListFilterConstraint withFilterExpression(PrismResourceListFilterExpression filterExpression) {
        this.filterExpression = filterExpression;
        return this;
    }

    public ResourceListFilterConstraint withNegated(Boolean negated) {
        this.negated = negated;
        return this;
    }

    public ResourceListFilterConstraint withDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
        return this;
    }

    public ResourceListFilterConstraint withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    public ResourceListFilterConstraint withValueDateStart(LocalDate valueDateStart) {
        this.valueDateStart = valueDateStart;
        return this;
    }

    public ResourceListFilterConstraint withValueDateClose(LocalDate valueDateClose) {
        this.valueDateClose = valueDateClose;
        return this;
    }

    public ResourceListFilterConstraint withValueDecimalStart(BigDecimal valueDecimalStart) {
        this.valueDecimalStart = valueDecimalStart;
        return this;
    }

    public ResourceListFilterConstraint withValueDecimalClose(BigDecimal valueDecimalClose) {
        this.valueDecimalClose = valueDecimalClose;
        return this;
    }

}
