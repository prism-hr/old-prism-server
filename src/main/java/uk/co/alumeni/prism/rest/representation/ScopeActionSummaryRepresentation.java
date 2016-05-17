package uk.co.alumeni.prism.rest.representation;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

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

        private Integer actionCount;

        public PrismAction getAction() {
            return action;
        }

        public void setAction(PrismAction action) {
            this.action = action;
        }

        public Integer getActionCount() {
            return actionCount;
        }

        public void setActionCount(Integer actionCount) {
            this.actionCount = actionCount;
        }

        public ActionSummaryRepresentation withAction(PrismAction action) {
            this.action = action;
            return this;
        }

        public ActionSummaryRepresentation withActionCount(Integer actionCount) {
            this.actionCount = actionCount;
            return this;
        }

    }

}
