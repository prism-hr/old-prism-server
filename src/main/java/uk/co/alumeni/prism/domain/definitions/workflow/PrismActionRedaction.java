package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismActionRedaction {

    private PrismRole role;

    private PrismActionRedactionType redactionType;

    public PrismRole getRole() {
        return role;
    }

    public PrismActionRedactionType getRedactionType() {
        return redactionType;
    }

    public PrismActionRedaction withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismActionRedaction withRedactionType(PrismActionRedactionType redactionType) {
        this.redactionType = redactionType;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, redactionType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrismActionRedaction other = (PrismActionRedaction) obj;
        return Objects.equal(role, other.getRole()) && Objects.equal(redactionType, other.getRedactionType());
    }

}
