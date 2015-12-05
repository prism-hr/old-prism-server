package uk.co.alumeni.prism.rest.representation.action;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;

public class ActionRepresentationExtended extends ActionRepresentationSimple {

    private boolean actionSequenceStart;

    private boolean actionSequenceClose;

    private Set<PrismActionEnhancement> actionEnhancements = Sets.newLinkedHashSet();

    private Set<StateRepresentationExtended> nextStates = Sets.newLinkedHashSet();

    private Set<StateRepresentationSimple> recommendedNextStates = Sets.newLinkedHashSet();

    public boolean isActionSequenceStart() {
        return actionSequenceStart;
    }

    public boolean isActionSequenceClose() {
        return actionSequenceClose;
    }

    public Set<PrismActionEnhancement> getActionEnhancements() {
        return actionEnhancements;
    }

    public Set<StateRepresentationExtended> getNextStates() {
        return nextStates;
    }

    public Set<StateRepresentationSimple> getRecommendedNextStates() {
        return recommendedNextStates;
    }

    public ActionRepresentationExtended withActionSequenceStart(boolean actionSequenceStart) {
        this.actionSequenceStart = actionSequenceStart;
        return this;
    }

    public ActionRepresentationExtended withActionSequenceClose(boolean actionSequenceClose) {
        this.actionSequenceClose = actionSequenceClose;
        return this;
    }

    public ActionRepresentationExtended addActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancements.add(actionEnhancement);
        return this;
    }

    public ActionRepresentationExtended addActionEnhancements(Collection<PrismActionEnhancement> actionEnhancements) {
        this.actionEnhancements.addAll(actionEnhancements);
        return this;
    }

    public ActionRepresentationExtended addNextStates(Collection<StateRepresentationExtended> nextStates) {
        this.nextStates.addAll(nextStates);
        return this;
    }

    public ActionRepresentationExtended addRecommendedNextStates(Collection<StateRepresentationSimple> recommendedNextStates) {
        this.recommendedNextStates.addAll(recommendedNextStates);
        return this;
    }

}
