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
import javax.validation.Valid;

import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;

@Entity(name = "APPLICATIONS_FILTER")
public class ApplicationsFilter implements Serializable {

    private static final long serialVersionUID = -2766208328669781519L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Valid
    private RegisteredUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_predicate")
    private SearchPredicate searchPredicate;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_category")
    private SearchCategory searchCategory;

    @Column(name = "search_term")
    private String searchTerm;

    public ApplicationsFilter() {
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((searchCategory == null) ? 0 : searchCategory.hashCode());
        result = prime * result + ((searchPredicate == null) ? 0 : searchPredicate.hashCode());
        result = prime * result + ((searchTerm == null) ? 0 : searchTerm.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApplicationsFilter other = (ApplicationsFilter) obj;
        if (searchCategory != other.searchCategory)
            return false;
        if (searchPredicate != other.searchPredicate)
            return false;
        if (searchTerm == null) {
            if (other.searchTerm != null)
                return false;
        } else if (!searchTerm.equals(other.searchTerm))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
}
