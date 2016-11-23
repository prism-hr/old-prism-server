package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.Definition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory;

import javax.persistence.*;

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

    @Column(name = "default_shared", nullable = false)
    private Boolean defaultShared;

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

    public Boolean getDefaultShared() {
        return defaultShared;
    }

    public void setDefaultShared(Boolean defaultShared) {
        this.defaultShared = defaultShared;
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

    public Scope withDefaultShared(Boolean defaultShared) {
        this.defaultShared = defaultShared;
        return this;
    }

    public Scope withOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
        return this;
    }

}
