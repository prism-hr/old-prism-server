package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.google.common.base.Objects;

public class PrismStateActionAssignment {

    private PrismRole role;

    private Boolean externalMode = false;

    private PrismActionEnhancement actionEnhancement;

    private Set<PrismStateActionRecipient> stateActionRecipients = newHashSet();

    public PrismRole getRole() {
        return role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public Set<PrismStateActionRecipient> getStateActionRecipients() {
        return stateActionRecipients;
    }

    public PrismStateActionAssignment withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateActionAssignment withExternalMode() {
        this.externalMode = true;
        return this;
    }

    public PrismStateActionAssignment withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    public PrismStateActionAssignment withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    public PrismStateActionAssignment withRecipients(PrismRole... roles) {
        for (PrismRole role : roles) {
            this.stateActionRecipients.add(new PrismStateActionRecipient().withRole(role));
        }
        return this;
    }

    public PrismStateActionAssignment withPartnerRecipients(PrismRole... roles) {
        for (PrismRole role : roles) {
            this.stateActionRecipients.add(new PrismStateActionRecipient().withRole(role).withExternalMode());
        }
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, externalMode, actionEnhancement);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismStateActionAssignment other = (PrismStateActionAssignment) object;
        return equal(role, other.getRole()) && equal(externalMode, other.getExternalMode()) && equal(actionEnhancement, other.getActionEnhancement());
    }

}
