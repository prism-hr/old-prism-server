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

    private Set<StateTransitionRepresentation> stateTransitions = Sets.newLinkedHashSet();

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

    public final Set<StateTransitionRepresentation> getStateTransitions() {
        return stateTransitions;
    }

    public final void addStateTransition(StateTransitionRepresentation stateTransition) {
        stateTransitions.add(stateTransition);
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

    public static class StateTransitionRepresentation {

        private PrismState transitionStateId;

        private Boolean parallelizable;

        public final PrismState getTransitionStateId() {
            return transitionStateId;
        }

        public final Boolean getParallelizable() {
            return parallelizable;
        }

        public ActionRepresentation.StateTransitionRepresentation withTransitionStateId(PrismState transitionStateId) {
            this.transitionStateId = transitionStateId;
            return this;
        }

        public ActionRepresentation.StateTransitionRepresentation withParallelizable(Boolean parallelizable) {
            this.parallelizable = parallelizable;
            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(transitionStateId);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final StateTransitionRepresentation other = (StateTransitionRepresentation) object;
            return Objects.equal(transitionStateId, other.getTransitionStateId());
        }

    }

}
