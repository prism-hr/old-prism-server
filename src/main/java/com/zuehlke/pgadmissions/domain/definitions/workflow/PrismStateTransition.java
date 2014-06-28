package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.collect.Lists;


public class PrismStateTransition {

    private PrismState transitionState;
    
    private PrismAction transitionAction;
    
    private PrismTransitionEvaluation evaluation;
    
    boolean postComment;
    
    private List<PrismRoleTransition> roleTransitions = Lists.newArrayList();
    
    private List<PrismAction> propagatedActions = Lists.newArrayList();

    public PrismState getTransitionState() {
        return transitionState;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public PrismTransitionEvaluation getEvaluation() {
        return evaluation;
    }

    public boolean isPostComment() {
        return postComment;
    }
    
    public List<PrismRoleTransition> getRoleTransitions() {
        return roleTransitions;
    }

    public List<PrismAction> getPropagatedActions() {
        return propagatedActions;
    }

    public PrismStateTransition withTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
        return this;
    }
    
    public PrismStateTransition withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }
    
    public PrismStateTransition withEvaluation(PrismTransitionEvaluation evaluation) {
        this.evaluation = evaluation;
        return this;
    }
    
    public PrismStateTransition withPostComment(boolean postComment) {
        this.postComment = postComment;
        return this;
    }
    
    public PrismStateTransition withRoleTransitions(List<PrismRoleTransition> roleTransitions) {
        this.roleTransitions = roleTransitions;
        return this;
    }
    
    public PrismStateTransition withPropagatedActions(List<PrismAction> propagatedActions) {
        this.propagatedActions = propagatedActions;
        return this;
    }
    
}
