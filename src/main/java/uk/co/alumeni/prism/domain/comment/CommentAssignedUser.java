package uk.co.alumeni.prism.domain.comment;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.workflow.user.CommentAssignedUserReassignmentProcessor;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "comment_assigned_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "user_id", "role_id", "role_transition_type"})})
public class CommentAssignedUser implements UniqueEntity, UserAssignment<CommentAssignedUserReassignmentProcessor> {

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
        return Objects.hashCode(comment, user, role, roleTransitionType);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        CommentAssignedUser other = (CommentAssignedUser) object;
        return equal(comment, other.getComment()) && equal(user, other.getUser()) && equal(role, other.getRole())
                && equal(roleTransitionType, other.getRoleTransitionType());
    }

    @Override
    public Class<CommentAssignedUserReassignmentProcessor> getUserReassignmentProcessor() {
        return CommentAssignedUserReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return comment.getResource().getUser().equals(user);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("comment", comment).addProperty("user", user).addProperty("role", role)
                .addProperty("roleTransitionType", roleTransitionType);
    }

}
