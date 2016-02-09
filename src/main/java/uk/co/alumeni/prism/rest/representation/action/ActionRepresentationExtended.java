package uk.co.alumeni.prism.rest.representation.action;

import java.util.Collection;
import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;

import com.google.common.collect.Sets;

public class ActionRepresentationExtended extends ActionRepresentationSimple {

    private CommentRepresentation comment;
    
    private Set<PrismActionEnhancement> actionEnhancements = Sets.newLinkedHashSet();

    private Set<StateRepresentationExtended> nextStates = Sets.newLinkedHashSet();

    private Set<StateRepresentationSimple> recommendedNextStates = Sets.newLinkedHashSet();
    
    public CommentRepresentation getComment() {
        return comment;
    }

    public void setComment(CommentRepresentation comment) {
        this.comment = comment;
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
