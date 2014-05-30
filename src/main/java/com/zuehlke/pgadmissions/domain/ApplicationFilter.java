package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

@Entity
@Table(name = "application_filter")
public class ApplicationFilter implements Serializable {

    private static final long serialVersionUID = -2766208328669781519L;

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_predicate", nullable = false)
    private SearchPredicate searchPredicate;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_category", nullable = false)
    private SearchCategory searchCategory;

    @Column(name = "search_term", nullable = false)
    private String searchTerm;
    
    @ManyToOne
    @JoinColumn(name = "application_filter_group_id", nullable = false, insertable = false, updatable = false)
    private ApplicationFilterGroup filterGroup;

    public ApplicationFilter() {
    }

    public SearchPredicate getSearchPredicate() {
        return searchPredicate;
    }

    public void setSearchPredicate(SearchPredicate searchPredicate) {
        this.searchPredicate = searchPredicate;
    }

    public SearchCategory getSearchCategory() {
        return searchCategory;
    }

    public void setSearchCategory(SearchCategory searchCategory) {
        this.searchCategory = searchCategory;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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
        final ApplicationFilter other = (ApplicationFilter) obj;

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
