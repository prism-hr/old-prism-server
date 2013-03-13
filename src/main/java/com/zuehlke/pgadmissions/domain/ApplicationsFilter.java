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
    @Column(name = "search_category")
    private SearchCategory searchCategory;

    @Column(name = "search_term")
    private String searchTerm;

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
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
}
