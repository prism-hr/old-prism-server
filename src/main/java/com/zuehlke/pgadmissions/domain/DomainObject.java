package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class DomainObject<T> implements Serializable {

	private static final int LARGE_PRIME = 3257;

	protected T id;

	public abstract void setId(T id);

	public abstract T getId();

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (id == null) {
			return false;
		}
		
		if (((DomainObject<?>) other).getId() == null) {
			return false;
		}
		
		if (this.getClass().isAssignableFrom(other.getClass())) {
		    // for LAYZ loaded objects
		    return id.equals(((DomainObject<?>) other).getId());
		}
		
		if (!this.getClass().equals(other.getClass())) {
			return false;
		}
		
		return id.equals(((DomainObject<?>) other).getId());
	}

	@Override
	public int hashCode() {
		if (id == null) {
			return LARGE_PRIME;
		}
		return LARGE_PRIME * id.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + getId() + "]";
	}
}
