package com.zuehlke.pgadmissions.services;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;

@Service
@Transactional
public class WorkflowConfigurationService {
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private StateService stateService;
    
    public String getWorkflowConfiguration() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        
        Document document = documentBuilder.newDocument();
        document.setXmlVersion("1.0");
        
        Element scopesElement = document.createElement("scopes");
        document.appendChild(scopesElement);
        
        for (Scope scopes : systemService.getAllScopes()) {
            Element resourceElement = buildScopeElement(document, scopes);  
            scopesElement.appendChild(resourceElement);
        }
        
        return document.toString();
    }

    private Element buildScopeElement(Document document, Scope scope) {
        Element scopeElement = document.createElement("scope");
        scopeElement.setAttribute("id", scope.getId().toString());
        scopeElement.setAttribute("precedence", scope.getPrecedence().toString());
        
        if (!scope.getStates().isEmpty()) {
            Element statesElement = document.createElement("states");
            scopeElement.appendChild(statesElement);
            
            for (State state : scope.getStates()) {
                Element stateElement = buildStateElement(document, state);
                statesElement.appendChild(stateElement);
            }
        }
        
        return scopeElement;
    }
    
    private Element buildStateElement(Document document, State state) {
        Element stateElement = document.createElement("state");
        stateElement.setAttribute("id", state.getId().toString());
        stateElement.setAttribute("parent-state", state.getParentState().toString());
        
        if (!state.getStateActions().isEmpty()) {
            Element actionsElement = document.createElement("actions");
            stateElement.appendChild(actionsElement);
            
            for (StateAction stateAction : state.getStateActions()) {
                Element actionElement = buildActionElement(document, stateAction);
                actionsElement.appendChild(actionElement);
            }
        }
        
        return stateElement;
    }

    private Element buildActionElement(Document document, StateAction stateAction) {
        Action action = stateAction.getAction();
        Element actionElement = document.createElement("action");
        actionElement.setAttribute("id", action.getId().toString());
        actionElement.setAttribute("type", action.getActionType().toString());
        actionElement.setAttribute("urgent", getXmlBoolean(stateAction.isRaisesUrgentFlag()));
        actionElement.setAttribute("default", getXmlBoolean(stateAction.isDefaultAction()));
        
        Integer precedence = stateAction.getPrecedence();
        if (precedence != null) {
            actionElement.setAttribute("precedence", precedence.toString());
        }
        
        NotificationTemplate notificationTemplate = stateAction.getNotificationTemplate();
        if (notificationTemplate != null) {
            Element notificationTemplateElement = buildNotificationTemplateElement(document, notificationTemplate);
            actionElement.appendChild(notificationTemplateElement);
        }
        
        Action delegateAction = action.getDelegateAction();
        if (delegateAction != null) {
            actionElement.setAttribute("delegate-action", action.getDelegateAction().getId().toString());
        }
        
        if (!stateAction.getStateActionAssignments().isEmpty()) {
            Element rolesElement = document.createElement("roles");
            actionElement.appendChild(rolesElement);
            
            for (StateActionAssignment stateActionAssignment : stateAction.getStateActionAssignments()) {
                Element roleElement = buildRoleElement(document, stateActionAssignment);
                rolesElement.appendChild(roleElement);
            }
        }
        
        Element nextAppend = actionElement;
        StateTransitionEvaluation stateTransitionEvaluation = stateService.getStateTransitionEvaluationByStateAction(stateAction);
        if (stateTransitionEvaluation != null) {
            Element stateTransitionEvaluationElement = buildStateTransitionEvaluationElement(document, actionElement, stateTransitionEvaluation);
            actionElement.appendChild(stateTransitionEvaluationElement);
            nextAppend = stateTransitionEvaluationElement;
        }
        
        if (!stateAction.getStateTransitions().isEmpty()) {
            Element transitionStatesElement = document.createElement("state-transitions");
            nextAppend.appendChild(transitionStatesElement);
                
            for (StateTransition stateTransition : stateAction.getStateTransitions()) {
                Element stateTransitionElement = buildStateTransitionElement(document, stateTransition);
                transitionStatesElement.appendChild(stateTransitionElement);
            }
        }
        
        if (!stateAction.getStateActionNotifications().isEmpty()) {
            Element stateActionNotificationsElement = document.createElement("update-notifications");
            actionElement.appendChild(stateActionNotificationsElement);
            
            for (StateActionNotification stateActionNotification : stateAction.getStateActionNotifications()) {
                Element stateActionNotificationElement = buildStateActionNotificationElement(document, stateActionNotification);
                stateActionNotificationsElement.appendChild(stateActionNotificationElement);
            }
        }
        
        return actionElement;
    }

    private Element buildStateActionNotificationElement(Document document, StateActionNotification stateActionNotification) {
        Element stateActionNotificationElement = document.createElement("update-notification");
        stateActionNotificationElement.setAttribute("id", stateActionNotification.getNotificationTemplate().getId().toString());
        stateActionNotificationElement.setAttribute("type", stateActionNotification.getNotificationTemplate().getNotificationType().toString());
        stateActionNotificationElement.setAttribute("role", stateActionNotification.getRole().getId().toString());
        return stateActionNotificationElement;
    }

    private Element buildStateTransitionEvaluationElement(Document document, Element actionElement, StateTransitionEvaluation stateTransitionEvaluation) {
        Element stateTransitionEvaluationElement = document.createElement("state-transition-evaluation");
        stateTransitionEvaluationElement.setAttribute("id", stateTransitionEvaluation.toString());
        actionElement.appendChild(stateTransitionEvaluationElement);
        return stateTransitionEvaluationElement;
    }

    private Element buildNotificationTemplateElement(Document document, NotificationTemplate notificationTemplate) {
        Element notificationTemplateElement = document.createElement("task-notification");
        notificationTemplateElement.setAttribute("id", notificationTemplate.getId().toString());
        notificationTemplateElement.setAttribute("type", notificationTemplate.getNotificationType().toString());
        
        NotificationTemplate reminderNotificationTemplate = notificationTemplate.getReminderTemplate();
        if(reminderNotificationTemplate != null) {
            Element reminderNotificationTemplateElement = document.createElement("task-reminder");
            reminderNotificationTemplateElement.setAttribute("id", reminderNotificationTemplate.getId().toString());
            reminderNotificationTemplateElement.setAttribute("type", reminderNotificationTemplate.getNotificationType().toString());
            notificationTemplateElement.appendChild(reminderNotificationTemplateElement);
        }
        
        return notificationTemplateElement;
    }
    
    private Element buildRoleElement(Document document, StateActionAssignment stateActionAssignment) {
        Role role = stateActionAssignment.getRole();
        Element roleElement = document.createElement("role");
        roleElement.setAttribute("id", role.getId().toString());
        return roleElement;
    }
    
    private Element buildStateTransitionElement(Document document, StateTransition stateTransition) {
        Element stateTransitionElement = document.createElement("transition-state");
        stateTransitionElement.setAttribute("id", stateTransition.getTransitionState().getId().toString());
        stateTransitionElement.setAttribute("transition-action", stateTransition.getTransitionAction().getId().toString());
        stateTransitionElement.setAttribute("post-comment", getXmlBoolean(stateTransition.isDoPostComment()));
        
        Integer displayOrder = stateTransition.getDisplayOrder();
        if (displayOrder != null) {
            stateTransitionElement.setAttribute("display-order", displayOrder.toString());
        }
        
        if (!stateTransition.getRoleTransitions().isEmpty()) {
            Element roleTransitionsElement = document.createElement("role-transitions");
            stateTransitionElement.appendChild(roleTransitionsElement);
            
            for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
                Element roleTransitionElement = buildRoleTransitionElement(document, roleTransition);
                roleTransitionsElement.appendChild(roleTransitionElement);
            }
        }
        
        if (!stateTransition.getPropagatedActions().isEmpty()) {
            Element propagatedActionsElement = document.createElement("propagated-actions");
            stateTransitionElement.appendChild(propagatedActionsElement);
            
            for (Action propagatedAction : stateTransition.getPropagatedActions()) {
                Element propagatedActionElement = buildPropagatedActionElement(document, propagatedAction);
                propagatedActionsElement.appendChild(propagatedActionElement);
            }
        }
        
        return stateTransitionElement;
    }
    
    private Element buildPropagatedActionElement(Document document, Action propagatedAction) {
        Element propagatedActionElement = document.createElement("propagated-action");
        propagatedActionElement.setAttribute("id", propagatedAction.getId().toString());
        return propagatedActionElement;
    }

    private Element buildRoleTransitionElement(Document document, RoleTransition roleTransition) {
        Element roleTransitionElement = document.createElement("role-transition");
        roleTransitionElement.setAttribute("id", roleTransition.getRole().getId().toString());
        roleTransitionElement.setAttribute("type", roleTransition.getRole().getId().toString());
        roleTransitionElement.setAttribute("restrict-to-owner", getXmlBoolean(roleTransition.isRestrictToActionOwner()));
        roleTransitionElement.setAttribute("minimum", roleTransition.getMinimumPermitted().toString());
        roleTransitionElement.setAttribute("maximum", roleTransition.getMaximumPermitted().toString());
        
        Role transitionRole = roleTransition.getTransitionRole();
        Element transitionRoleElement = document.createElement("transition-role");
        transitionRoleElement.setAttribute("id", transitionRole.toString());
        
        if (!transitionRole.getExcludedRoles().isEmpty()) {
            Element roleTransitionExclusionsElement = document.createElement("exclusions");
            transitionRoleElement.appendChild(roleTransitionExclusionsElement);
            
            for (Role excludedRole : transitionRole.getExcludedRoles()) {
                Element roleTransitionExclusionElement = buildRoleTransitionExclusionElement(document, excludedRole);
                roleTransitionExclusionsElement.appendChild(roleTransitionExclusionElement);
            }
            
        }
        
        roleTransitionElement.appendChild(transitionRoleElement);
        
        return roleTransitionElement;
    }

    private Element buildRoleTransitionExclusionElement(Document document, Role excludedRole) {
        Element roleTransitionExclusionElement = document.createElement("exclusion");
        roleTransitionExclusionElement.setAttribute("id", excludedRole.getId().toString());
        return roleTransitionExclusionElement;
    }

    private String getXmlBoolean(boolean javaBoolean) {
        return javaBoolean ? "true" : "false";
    }
    
}
