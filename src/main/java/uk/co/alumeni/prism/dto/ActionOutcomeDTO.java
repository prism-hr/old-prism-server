package uk.co.alumeni.prism.dto;

import java.util.List;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.StateTransition;

public class ActionOutcomeDTO {

    private User user;

    private Resource resource;

    private Resource transitionResource;

    private Action transitionAction;

    private StateTransition stateTransition;

    private List<Comment> replicableSequenceComments;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getTransitionResource() {
        return transitionResource;
    }

    public void setTransitionResource(Resource transitionResource) {
        this.transitionResource = transitionResource;
    }

    public Action getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public List<Comment> getReplicableSequenceComments() {
        return replicableSequenceComments;
    }

    public void setReplicableSequenceComments(List<Comment> replicableSequenceComments) {
        this.replicableSequenceComments = replicableSequenceComments;
    }

    public ActionOutcomeDTO withUser(User user) {
        this.user = user;
        return this;
    }

    public ActionOutcomeDTO withResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public ActionOutcomeDTO withTransitionResource(Resource transitionResource) {
        this.transitionResource = transitionResource;
        return this;
    }

    public ActionOutcomeDTO withTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public ActionOutcomeDTO withStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

}
