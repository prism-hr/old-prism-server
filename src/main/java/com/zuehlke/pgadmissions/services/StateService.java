package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;

@Service
@Transactional
public class StateService {

    @Autowired
    private StateDAO stateDAO;

    public State getById(PrismState id) {
        return stateDAO.getById(id);
    }

    public void save(State state) {
        stateDAO.save(state);
    }

    public List<State> getAllConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }
    
    public List<StateTransition> getStateTransitions(PrismResource resource, PrismAction action) {
        return stateDAO.getStateTransitions(resource, action);
    }
    
    public StateTransition getStateTransition(PrismResource resource, PrismAction action, Comment comment) {
        StateTransition stateTransition = null;
        
        List<StateTransition> stateTransitions = getStateTransitions(resource, action);     
        if (stateTransitions.size() > 1) {
            try {
                String method = stateTransitions.get(0).getEvaluation().getMethodName(); 
                stateTransition = (StateTransition) MethodUtils.invokeExactMethod(this, method, new Object[] {resource, comment, stateTransitions});
            } catch (Exception e) {
                
            }
        } else {
            stateTransition = stateTransitions.get(0);
        }
        
        return stateTransition;
    }

    public StateTransition getApplicationCompletedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        try {
            Application application = (Application) resource;
            if (application.getSubmittedTimestamp() != null) {
                transitionState = stateDAO.getById(PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK);
            }
        } catch (ClassCastException e) {
            throw new Error(StateTransitionEvaluation.INCORRECT_PROCESSOR_TYPE, e);
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }
    
    public StateTransition getApplicationEligibilityAssessedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        PrismState transitionState = PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
        if (comment.isAtLeastOneAnswerUnsure()) {
            transitionState = PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK;
        }
        return stateDAO.getStateTransition(stateTransitions, stateDAO.getById(transitionState));
    }
    
    public StateTransition getApplicationExportedOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState = resource.getState();
        State parentState = transitionState.getParentState();
        if (comment.getExportError() != null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_PENDING_CORRECTION"));
        } else if (comment.getExportResponse() != null && comment.getExportError() == null) {
            transitionState = stateDAO.getById(PrismState.valueOf(parentState.toString() + "_COMPLETED"));          
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }
    
    public StateTransition getInterviewScheduledOutcome(PrismResource resource, Comment comment, List<StateTransition> stateTransitions) {
        State transitionState;
        DateTime baselineDateTime = new DateTime();
        DateTime interviewDateTime = comment.getInterviewDateTime();
        if (interviewDateTime != null) {
            if (interviewDateTime.isEqual(baselineDateTime) || interviewDateTime.isBefore(baselineDateTime)) {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK);
            } else {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW);
            }
        } else {
            if (resource.getState().getId() == PrismState.APPLICATION_INTERVIEW) {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY);
            } else {
                transitionState = stateDAO.getById(PrismState.APPLICATION_INTERVIEW);               
            }
        }
        return stateDAO.getStateTransition(stateTransitions, transitionState);
    }
    
}
