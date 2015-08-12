package com.zuehlke.pgadmissions.rest.representation.action;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;

public class ActionRepresentationExtended extends ActionRepresentationSimple {

    private Set<PrismActionEnhancement> actionEnhancements = Sets.newLinkedHashSet();

    private Set<StateRepresentationExtended> nextStates = Sets.newLinkedHashSet();

    private Set<StateRepresentationSimple> recommendedNextStates = Sets.newLinkedHashSet();

    public Set<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }

    public ActionRepresentationExtended addActionEnhancements(Collection<PrismActionEnhancement> actionEnhancement) {
        actionEnhancements.addAll(actionEnhancement);
        return this;
    }

    public Set<StateRepresentationExtended> getNextStates() {
        return nextStates;
    }

    public ActionRepresentationExtended addNextStates(Collection<StateRepresentationExtended> nextStates) {
        this.nextStates.addAll(nextStates);
        return this;
    }

    public Set<StateRepresentationSimple> getRecommendedNextStates() {
        return recommendedNextStates;
    }

    public ActionRepresentationExtended addRecommendedNextStates(Collection<StateRepresentationSimple> recommendedNextStates) {
        this.recommendedNextStates.addAll(recommendedNextStates);
        return this;
    }

}