package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

public class PrismStateActionRecipient {

    private PrismRole role;

    private Boolean externalMode = false;

    public PrismRole getRole() {
        return role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public PrismStateActionRecipient withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateActionRecipient withExternalMode() {
        this.externalMode = true;
        return this;
    }

    public PrismStateActionRecipient withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, externalMode);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismStateActionRecipient other = (PrismStateActionRecipient) object;
        return equal(role, other.getRole()) && equal(externalMode, other.getExternalMode());
    }

}
