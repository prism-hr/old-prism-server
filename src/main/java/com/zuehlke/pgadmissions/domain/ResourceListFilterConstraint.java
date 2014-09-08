package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;

@Entity
@Table(name = "RESOURCE_LIST_FILTER_CONSTRAINT", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "resouce_list_filter_id", "filter_property", "filter_term_string" }),
        @UniqueConstraint(columnNames = { "resouce_list_filter_id", "filter_property", "filter_term_date_start", "filter_term_date_close" }),
        @UniqueConstraint(columnNames = { "resouce_list_filter_id", "filter_property", "filter_term_state_id" }),
        @UniqueConstraint(columnNames = { "resouce_list_filter_id", "filter_property", "filter_term_decimal_start", "filter_term_decimal_close" }) })
public class ResourceListFilterConstraint {

    @Id
    @GeneratedValue
    private Integer Id;

    @JoinColumn(name = "resource_list_filter_id", nullable = false)
    private ResourceListFilter filter;

    @Column(name = "filter_property", nullable = false)
    @Enumerated(EnumType.STRING)
    private FilterProperty filterProperty;

    @Column(name = "negated", nullable = false)
    private Boolean negated;

    @Column(name = "filter_term_string")
    private String filterTermString;

    @Column(name = "filter_term_date_start")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate filterTermDateStart;

    @Column(name = "filter_term_date_close")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate filterTermDateClose;

    @JoinColumn(name = "filter_term_state_group_id")
    private StateGroup filterTermStateGroup;

    @Column(name = "filter_term_decimal_start")
    private BigDecimal filterTermDecimalStart;

    @Column(name = "filter_term_decimal_close")
    private BigDecimal filterTermDecimalClose;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RESOURCE_LIST_FILTER_CONSTRAINT_ROLE", joinColumns = { @JoinColumn(name = "resource_list_filter_constraint_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false) })
    private Set<Role> roles = Sets.newHashSet();

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

    public final FilterProperty getFilterProperty() {
        return filterProperty;
    }

    public final void setFilterProperty(FilterProperty filterProperty) {
        this.filterProperty = filterProperty;
    }

    public final Boolean isNegated() {
        return negated;
    }

    public final void setNegated(Boolean negated) {
        this.negated = negated;
    }

    public final String getFilterTermString() {
        return filterTermString;
    }

    public final void setFilterTermString(String filterTermString) {
        this.filterTermString = filterTermString;
    }

    public final LocalDate getFilterTermDateStart() {
        return filterTermDateStart;
    }

    public final void setFilterTermDateStart(LocalDate filterTermDateStart) {
        this.filterTermDateStart = filterTermDateStart;
    }

    public final LocalDate getFilterTermDateClose() {
        return filterTermDateClose;
    }

    public final void setFilterTermDateClose(LocalDate filterTermDateClose) {
        this.filterTermDateClose = filterTermDateClose;
    }

    public final StateGroup getFilterTermStateGroup() {
        return filterTermStateGroup;
    }

    public final void setFilterTermStateGroup(StateGroup filterTermStateGroup) {
        this.filterTermStateGroup = filterTermStateGroup;
    }

    public final BigDecimal getFilterTermDecimalStart() {
        return filterTermDecimalStart;
    }

    public final void setFilterTermDecimalStart(BigDecimal filterTermDecimalStart) {
        this.filterTermDecimalStart = filterTermDecimalStart;
    }

    public final BigDecimal getFilterTermDecimalClose() {
        return filterTermDecimalClose;
    }

    public final void setFilterTermDecimalClose(BigDecimal filterTermDecimalClose) {
        this.filterTermDecimalClose = filterTermDecimalClose;
    }

    public final Set<Role> getRoles() {
        return roles;
    }

    public ResourceListFilterConstraint withFilter(ResourceListFilter filter) {
        this.filter = filter;
        return this;
    }

    public ResourceListFilterConstraint withFilterProperty(FilterProperty filterProperty) {
        this.filterProperty = filterProperty;
        return this;
    }

    public ResourceListFilterConstraint withNegated(Boolean negated) {
        this.negated = negated;
        return this;
    }

}
