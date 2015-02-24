package com.zuehlke.pgadmissions.domain.resource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Role;

@Entity
@Table(name = "RESOURCE_BATCH_ASSIGNED_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "resource_batch_id", "user_id", "role_id" }) })
public class ResourceBatchAssignedUser implements IUniqueEntity {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "resource_batch_id", nullable = false, insertable = false, updatable = false)
	private ResourceBatch resourceBatch;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

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

	public ResourceBatch getResourceBatch() {
		return resourceBatch;
	}

	public void setResourceBatch(ResourceBatch resourceBatch) {
		this.resourceBatch = resourceBatch;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ResourceBatchAssignedUser withUser(User user) {
		this.user = user;
		return this;
	}

	public ResourceBatchAssignedUser withRole(final Role role) {
		this.role = role;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(resourceBatch, user, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ResourceBatchAssignedUser other = (ResourceBatchAssignedUser) obj;
		return Objects.equal(resourceBatch, other.getResourceBatch()) && Objects.equal(user, other.getUser()) && Objects.equal(role, other.getRole());
	}

	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("resourceBatch", resourceBatch).addProperty("user", user).addProperty("role", role);
	}

}
