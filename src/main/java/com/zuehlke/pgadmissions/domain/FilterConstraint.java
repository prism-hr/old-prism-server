package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.ApplicationListFilterCategory;
import com.zuehlke.pgadmissions.domain.enums.ResourceListSearchPredicate;

@Entity
@Table(name = "FILTER_CONSTRAINT", uniqueConstraints = { @UniqueConstraint(columnNames = { "filter_id", "filter_position_id" }) })
public class FilterConstraint {

    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(name = "filter_position_id", nullable = false)
    private int filterPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_predicate", nullable = false)
    private ResourceListSearchPredicate searchPredicate;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_category", nullable = false)
    private ApplicationListFilterCategory searchCategory;

    @Column(name = "search_term", nullable = false)
    private String searchTerm;

    public FilterConstraint() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public int getFilterPosition() {
        return filterPosition;
    }

    public void setFilterPosition(int filterPosition) {
        this.filterPosition = filterPosition;
    }

    public ResourceListSearchPredicate getSearchPredicate() {
        return searchPredicate;
    }

    public void setSearchPredicate(ResourceListSearchPredicate searchPredicate) {
        this.searchPredicate = searchPredicate;
    }

    public ApplicationListFilterCategory getSearchCategory() {
        return searchCategory;
    }

    public void setSearchCategory(ApplicationListFilterCategory searchCategory) {
        this.searchCategory = searchCategory;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(searchCategory, searchPredicate, searchTerm);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FilterConstraint other = (FilterConstraint) obj;

        return Objects.equal(this.searchCategory, other.searchCategory) && Objects.equal(this.searchPredicate, other.searchPredicate)
                && Objects.equal(this.searchTerm, other.searchTerm);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(searchCategory != null ? searchCategory.displayValue() : "<empty>");
        sb.append(" ");
        sb.append(searchPredicate != null ? searchPredicate.displayValue() : "<empty>");
        sb.append(" ");
        sb.append(searchTerm != null ? "\"" + searchTerm + "\"" : "<empty>");
        return sb.toString();
    }
}
