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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Entity
@Table(name = "application_filter_group")
public class ApplicationFilterGroup implements Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_filter_group_id")
    @OrderColumn(name = "filter_position")
    private List<ApplicationFilter> filters = new ArrayList<ApplicationFilter>();

    @Column(name = "satisfy_all_conditions")
    private Boolean satisfyAllConditions = false;

    @Column(name = "sort_category")
    @Enumerated(EnumType.STRING)
    private SortCategory sortCategory = SortCategory.APPLICATION_DATE;

    @Column(name = "sort_order")
    @Enumerated(EnumType.STRING)
    private SortOrder order = SortOrder.DESCENDING;

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

    public Boolean getUseDisjunction() {
        return satisfyAllConditions;
    }

    public void setUseDisjunction(Boolean useDisjunction) {
        this.satisfyAllConditions = useDisjunction;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ApplicationFilter> getFilters() {
        return filters;
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