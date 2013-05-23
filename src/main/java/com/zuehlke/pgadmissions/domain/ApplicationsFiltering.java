package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.IndexColumn;

import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Entity(name = "APPLICATIONS_FILTERING")
public class ApplicationsFiltering implements Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "filtering_id")
    @IndexColumn(name = "filter_position")
    private List<ApplicationsFilter> filters = new ArrayList<ApplicationsFilter>();

    @Column(name = "pre_filter")
    @Enumerated(EnumType.STRING)
    private ApplicationsPreFilter preFilter = ApplicationsPreFilter.MY;
    
    @Transient
    private SortCategory sortCategory = SortCategory.APPLICATION_DATE;

    @Transient
    private SortOrder order = SortOrder.ASCENDING;

    @Transient
    private Integer blockCount = 1;
    
    @Transient
    private Integer latestConsideredFlagIndex = 0;
    
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

    public List<ApplicationsFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ApplicationsFilter> filters) {
        this.filters = filters;
    }

    public ApplicationsPreFilter getPreFilter() {
        return preFilter;
    }

    public void setPreFilter(ApplicationsPreFilter preFilter) {
        this.preFilter = preFilter;
    }

    public SortCategory getSortCategory() {
        return sortCategory;
    }

    public void setSortCategory(SortCategory sortCategory) {
        this.sortCategory = sortCategory;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
    }
    
    

}
