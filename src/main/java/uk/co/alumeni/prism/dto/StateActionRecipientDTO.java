package uk.co.alumeni.prism.dto;

import static org.apache.commons.lang3.ObjectUtils.compare;
import static uk.co.alumeni.prism.utils.PrismComparisonUtils.compareRoles;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public class StateActionRecipientDTO implements Comparable<StateActionRecipientDTO> {

    private PrismRole role;

    private Boolean externalMode;

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    @Override
    public int compareTo(StateActionRecipientDTO other) {
        int compare = compare(externalMode, other.getExternalMode());
        return compare == 0 ? compareRoles(role, other.getRole()) : compare;
    }

}
