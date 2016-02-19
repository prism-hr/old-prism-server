package uk.co.alumeni.prism.rest.representation.action;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Collection;
import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ActionRepresentationExtended extends ActionRepresentationSimple {

    private CommentRepresentation comment;

    private Set<PrismActionEnhancement> actionEnhancements = newLinkedHashSet();

    private Set<StateRepresentationExtended> nextStates = newLinkedHashSet();

    private Set<StateRepresentationSimple> recommendedNextStates = newLinkedHashSet();

    private Set<UserRepresentationSimple> messagableUsers = newLinkedHashSet();
    
    private Set<PrismRole> messagableRoles = newLinkedHashSet();

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

    public Set<UserRepresentationSimple> getMessagableUsers() {
        return messagableUsers;
    }

    public Set<PrismRole> getMessagableRoles() {
        return messagableRoles;
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
    
    public ActionRepresentationExtended addMessagableUsers(Collection<UserRepresentationSimple> messagableUsers) {
        this.messagableUsers.addAll(messagableUsers);
        return this;
    }

    public ActionRepresentationExtended addMessagableRoles(Collection<PrismRole> messagableRoles) {
        this.messagableRoles.addAll(messagableRoles);
        return this;
    }

}
