package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

@Entity
@Table(name = "RESOURCE_LIST_FILTER", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "scope_id" }) })
public class ResourceListFilter implements IUniqueEntity {

    @Id
    @GeneratedValue
    Integer Id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Column(name = "urgent_only", nullable = false)
    private Boolean urgentOnly;

    @Column(name = "match_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private FilterMatchMode matchMode;

    @Column(name = "sort_order", nullable = false)
    @Enumerated(EnumType.STRING)
    private FilterSortOrder sortOrder;

    @Column(name = "value_string")
    private String valueString;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_list_filter_id", nullable = false)
    @OrderBy("displayPosition")
    private Set<ResourceListFilterConstraint> constraints = Sets.newHashSet();

    public final Integer getId() {
        return Id;
    }

    public final void setId(Integer id) {
        Id = id;
    }

    public final UserAccount getUserAccount() {
        return userAccount;
    }

    public final void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public final Scope getScope() {
        return scope;
    }

    public final void setScope(Scope scope) {
        this.scope = scope;
    }

    public final Boolean isUrgentOnly() {
        return urgentOnly;
    }

    public final void setUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
    }

    public final FilterMatchMode getMatchMode() {
        return matchMode;
    }

    public final void setMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public final FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final void setSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public final String getValueString() {
        return valueString;
    }

    public final void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public final Set<ResourceListFilterConstraint> getConstraints() {
        return constraints;
    }

    public ResourceListFilter withUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public ResourceListFilter withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public ResourceListFilter withUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
        return this;
    }

    public ResourceListFilter withMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public ResourceListFilter withSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ResourceListFilter withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("userAccount", userAccount);
        properties.put("scope", scope);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
