package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ScopeActionSummaryRepresentation {

    private PrismScope scope;

    private List<ActionSummaryRepresentation> actionSummaries;

    public List<ActionSummaryRepresentation> getActionSummaries() {
        return actionSummaries;
    }

    public void setActionSummaries(List<ActionSummaryRepresentation> actionSummaries) {
        this.actionSummaries = actionSummaries;
    }

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public ScopeActionSummaryRepresentation withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public ScopeActionSummaryRepresentation withActionSummaries(List<ActionSummaryRepresentation> actionSummaries) {
        this.actionSummaries = actionSummaries;
        return this;
    }

    public static class ActionSummaryRepresentation {

        private PrismAction action;

        private Long actionCount;

        public PrismAction getAction() {
            return action;
        }

        public void setAction(PrismAction action) {
            this.action = action;
        }

        public Long getActionCount() {
            return actionCount;
        }

        public void setActionCount(Long actionCount) {
            this.actionCount = actionCount;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(action);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            ActionSummaryRepresentation other = (ActionSummaryRepresentation) object;
            return action.equals(other.getAction());
        }

    }

}
