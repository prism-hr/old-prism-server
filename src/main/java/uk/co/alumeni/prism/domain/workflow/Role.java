package uk.co.alumeni.prism.domain.workflow;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.user.UserRole;

@Entity
@Table(name = "role")
public class Role extends WorkflowDefinition implements GrantedAuthority {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private PrismRole id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_category", nullable = false)
    private PrismRole.PrismRoleCategory roleCategory;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Column(name = "directly_assignable", nullable = false)
    private Boolean directlyAssignable;

    @Column(name = "scope_creator")
    private Boolean scopeCreator;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<StateActionAssignment> stateActionAssignments = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<StateActionNotification> stateActionNotifications = Sets.newHashSet();

    @OneToMany(mappedBy = "role")
    private Set<ActionRedaction> actionRedactions = Sets.newHashSet();

    @Override
    public PrismRole getId() {
        return id;
    }

    public void setId(PrismRole id) {
        this.id = id;
    }

    public PrismRole.PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(PrismRole.PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getDirectlyAssignable() {
        return directlyAssignable;
    }

    public void setDirectlyAssignable(Boolean directlyAssignable) {
        this.directlyAssignable = directlyAssignable;
    }

    public Boolean getScopeCreator() {
        return scopeCreator;
    }

    public void setScopeCreator(Boolean scopeCreator) {
        this.scopeCreator = scopeCreator;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Set<StateActionAssignment> getStateActionAssignments() {
        return stateActionAssignments;
    }

    public Set<StateActionNotification> getStateActionNotifications() {
        return stateActionNotifications;
    }

    public final Set<ActionRedaction> getActionRedactions() {
        return actionRedactions;
    }

    public Role withId(PrismRole id) {
        this.id = id;
        return this;
    }

    public Role withRoleCategory(PrismRole.PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
        return this;
    }

    public Role withVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public Role withDirectlyAssignable(Boolean directlyAssignable) {
        this.directlyAssignable = directlyAssignable;
        return this;
    }

    public Role withScopeCreator(Boolean scopeCreator) {
        this.scopeCreator = scopeCreator;
        return this;
    }

    public Role withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public String getAuthority() {
        return id.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Role other = (Role) object;
        return Objects.equal(id, other.getId());
    }

}
