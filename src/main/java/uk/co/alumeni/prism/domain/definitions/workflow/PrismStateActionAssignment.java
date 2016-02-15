package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class PrismStateActionAssignment {

    private PrismRole role;

    private Boolean externalMode = false;

    private PrismActionEnhancement actionEnhancement;

    private List<PrismRole> recipients = Lists.newArrayList();

    public PrismRole getRole() {
        return role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public List<PrismRole> getRecipients() {
        return recipients;
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

    public PrismStateActionAssignment addRecipient(PrismRole recipient) {
        recipients.add(recipient);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, externalMode, actionEnhancement, recipients);
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
        return Objects.equal(role, other.getRole()) && Objects.equal(externalMode, other.getExternalMode())
                && Objects.equal(actionEnhancement, other.getActionEnhancement()) && recipients.size() == other.getRecipients().size()
                && recipients.containsAll(other.getRecipients());
    }

}
