package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ActionRepresentation {

    private PrismAction name;

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    private Set<PrismActionEnhancement> actionEnhancements = Sets.newLinkedHashSet();

    private Set<NextStateRepresentation> nextStates = Sets.newLinkedHashSet();

    public PrismAction getName() {
        return name;
    }

    public final void setName(PrismAction name) {
        this.name = name;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public final void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final Set<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }

    public final void addActionEnhancement(PrismActionEnhancement actionEnhancement) {
        actionEnhancements.add(actionEnhancement);
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public ActionRepresentation withName(PrismAction name) {
        this.name = name;
        return this;
    }

    public ActionRepresentation withRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }

    public ActionRepresentation withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

    public final Set<NextStateRepresentation> getNextStates() {
        return nextStates;
    }

    public final void addNextState(NextStateRepresentation nextState) {
        nextStates.add(nextState);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ActionRepresentation other = (ActionRepresentation) object;
        return Objects.equal(name, other.getName());
    }

    public static class NextStateRepresentation {

        private PrismState state;

        private Boolean parallelizable;

        public final PrismState getState() {
            return state;
        }

        public final Boolean getParallelizable() {
            return parallelizable;
        }

        public ActionRepresentation.NextStateRepresentation withState(PrismState state) {
            this.state = state;
            return this;
        }

        public ActionRepresentation.NextStateRepresentation withParallelizable(Boolean parallelizable) {
            this.parallelizable = parallelizable;
            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(state);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final NextStateRepresentation other = (NextStateRepresentation) object;
            return Objects.equal(state, other.getState());
        }

    }

}
