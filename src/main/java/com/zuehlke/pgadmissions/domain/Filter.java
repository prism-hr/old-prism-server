package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationListSortCategory;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListSortOrder;

@Entity
@Table(name = "FILTER", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "scope_id" }) })
public class Filter {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_account_id", updatable = false, insertable = false)
    private UserAccount userAccount;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", updatable = false, insertable = false)
    private Scope scope;

    @Column(name = "satisfy_all_conditions", nullable = false)
    private Boolean satisfyAllConditions = false;

    @Column(name = "sort_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationListSortCategory sortCategory = ApplicationListSortCategory.APPLICATION_DATE;

    @Column(name = "sort_order", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceListSortOrder sortOrder = ResourceListSortOrder.DESCENDING;
    
    @Column(name = "last_access_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastAccessTimestamp;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "filter_id", nullable = false)
    @OrderColumn(name = "filter_position")
    private Set<FilterConstraint> filterConstraints = Sets.newHashSet();
    
    @Transient
    private Integer page = 1;

    @Transient
    private Integer latestConsideredFlagIndex = 0;
    
    public Filter() {
    }

    public Integer getLatestConsideredFlagIndex() {
        return latestConsideredFlagIndex;
    }

    public void setLatestConsideredFlagIndex(final Integer index) {
        this.latestConsideredFlagIndex = index;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Scope getScope() {
        return scope;
    }
    
    public Boolean isSatisfyAllConditions() {
        return satisfyAllConditions;
    }

    public void setSatisfyAllConditions(boolean satisfyAllConditions) {
        this.satisfyAllConditions = satisfyAllConditions;
    }

    public ApplicationListSortCategory getSortCategory() {
        return sortCategory;
    }

    public void setSortCategory(ApplicationListSortCategory sortCategory) {
        this.sortCategory = sortCategory;
    }

    public ResourceListSortOrder getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(ResourceListSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public DateTime getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    public void setLastAccessTimestamp(DateTime lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
    }

    public Set<FilterConstraint> getFilterConstraints() {
        return filterConstraints;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

}
