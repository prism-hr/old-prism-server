package uk.co.alumeni.prism.domain.resource;

import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode;
import uk.co.alumeni.prism.domain.definitions.PrismFilterSortOrder;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.workflow.Scope;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "resource_list_filter", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_account_id", "scope_id"})})
public class ResourceListFilter implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer Id;

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
    private PrismFilterMatchMode matchMode;

    @Column(name = "sort_order", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismFilterSortOrder sortOrder;

    @Column(name = "value_string")
    private String valueString;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resource_list_filter_id", nullable = false)
    @OrderBy("displayPosition")
    private Set<ResourceListFilterConstraint> constraints = Sets.newHashSet();

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Boolean isUrgentOnly() {
        return urgentOnly;
    }

    public void setUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
    }

    public PrismFilterMatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public PrismFilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(PrismFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Set<ResourceListFilterConstraint> getConstraints() {
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

    public ResourceListFilter withMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public ResourceListFilter withSortOrder(PrismFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ResourceListFilter withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("userAccount", userAccount).addProperty("scope", scope);
    }

}
