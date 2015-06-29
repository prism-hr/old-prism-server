package com.zuehlke.pgadmissions.rest.representation;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ActionRepresentation {

    private PrismAction id;

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    private Set<PrismActionEnhancement> actionEnhancements = Sets.newLinkedHashSet();

    private Set<SelectableStateRepresentation> nextStates = Sets.newLinkedHashSet();
    
    private Set<SelectableStateRepresentation> recommendedNextStates = Sets.newLinkedHashSet();

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }
    
    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public Set<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }
    
    public Set<SelectableStateRepresentation> getNextStates() {
        return nextStates;
    }

    public Set<SelectableStateRepresentation> getRecommendedNextStates() {
        return recommendedNextStates;
    }

    public ActionRepresentation withId(PrismAction id) {
        this.id = id;
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
    
    public ActionRepresentation addActionEnhancements(Collection<PrismActionEnhancement> actionEnhancement) {
        actionEnhancements.addAll(actionEnhancement);
        return this;
    }
    
    public ActionRepresentation addNextStates(Collection<SelectableStateRepresentation> nextStates) {
        this.nextStates.addAll(nextStates);
        return this;
    }
    
    public ActionRepresentation addRecommendedNextStates(Collection<SelectableStateRepresentation> recommendedNextStates) {
        this.recommendedNextStates.addAll(recommendedNextStates);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ActionRepresentation other = (ActionRepresentation) object;
        return Objects.equal(id, other.getId());
    }

    public static class SelectableStateRepresentation {

        private PrismState state;

        private Boolean parallelizable;

        public PrismState getState() {
            return state;
        }

        public Boolean getParallelizable() {
            return parallelizable;
        }

        public SelectableStateRepresentation withState(PrismState state) {
            this.state = state;
            return this;
        }

        public SelectableStateRepresentation withParallelizable(Boolean parallelizable) {
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
            SelectableStateRepresentation other = (SelectableStateRepresentation) object;
            return Objects.equal(state, other.getState());
        }

    }

}
