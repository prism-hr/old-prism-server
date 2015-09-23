package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.Definition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory;

@Entity
@Table(name = "scope")
public class Scope extends Definition<PrismScope> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;

    @Column(name = "scope_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScopeCategory scopeCategory;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    @Column(name = "ordinal", nullable = false, unique = true)
    private Integer ordinal;

    @Override
    public PrismScope getId() {
        return id;
    }

    @Override
    public void setId(PrismScope id) {
        this.id = id;
    }

    public PrismScopeCategory getScopeCategory() {
        return scopeCategory;
    }

    public void setScopeCategory(PrismScopeCategory scopeCategory) {
        this.scopeCategory = scopeCategory;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Scope withId(PrismScope id) {
        this.id = id;
        return this;
    }

    public Scope withScopeCategory(PrismScopeCategory scopeCategory) {
        this.scopeCategory = scopeCategory;
        return this;
    }

    public Scope withShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public Scope withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

}
