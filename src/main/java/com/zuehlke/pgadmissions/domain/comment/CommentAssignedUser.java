package com.zuehlke.pgadmissions.domain.comment;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Role;

import javax.persistence.*;

@Entity
@Table(name = "comment_assigned_user", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "user_id", "role_id" }) })
public class CommentAssignedUser implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "role_transition_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismRoleTransitionType roleTransitionType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public final PrismRoleTransitionType getRoleTransitionType() {
        return roleTransitionType;
    }

    public final void setRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
    }

    public CommentAssignedUser withUser(User user) {
        this.user = user;
        return this;
    }

    public CommentAssignedUser withRole(final Role role) {
        this.role = role;
        return this;
    }

    public CommentAssignedUser withRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, user, role);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CommentAssignedUser other = (CommentAssignedUser) obj;
        return Objects.equal(comment, other.getComment()) && Objects.equal(user, other.getUser()) && Objects.equal(role, other.getRole());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("comment", comment).addProperty("user", user).addProperty("role", role);
    }

}
