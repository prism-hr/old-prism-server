package com.zuehlke.pgadmissions.services;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;

@Service
@Transactional(readOnly = true)
public class WorkflowConfigurationExportService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private StateService stateService;

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    public String exportWorkflowConfiguration() throws Exception {
        DocumentBuilder documentBuilder = prepareDocumentBuilder();
        Document document = documentBuilder.newDocument();
        document.setXmlVersion("1.0");

        Element scopesElement = document.createElement("scopes");
        document.appendChild(scopesElement);

        for (Scope scopes : systemService.getAllScopes()) {
            Element resourceElement = buildScopeElement(document, scopes);
            scopesElement.appendChild(resourceElement);
        }

        return parseDocumentToString(document).trim();
    }

    private DocumentBuilder prepareDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        return documentBuilder;
    }

    private String parseDocumentToString(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    private Element buildScopeElement(Document document, Scope scope) {
        Element scopeElement = document.createElement("scope");
        scopeElement.setAttribute("id", scope.getId().toString());
        
        if (!scope.getScopeCreations().isEmpty()) {
            buildDescendentScopesElement(document, scope, scopeElement);
        }

        if (!scope.getStates().isEmpty()) {
            buildStatesElement(document, scope, scopeElement);
        }

        return scopeElement;
    }

    private void buildDescendentScopesElement(Document document, Scope scope, Element scopeElement) {
        Element descendentScopesElement = document.createElement("descendent-scopes");
        scopeElement.appendChild(descendentScopesElement);
        
        for (Scope scopeCreation : scope.getScopeCreations()) {
            Element scopeCreationElement = buildDescendentScopeElement(document, scope, scopeCreation);
            descendentScopesElement.appendChild(scopeCreationElement);
        }
    }

    private Element buildDescendentScopeElement(Document document, Scope scope, Scope scopeCreation) {
        Element descendentScopeElement = document.createElement("descendent-scope");
        descendentScopeElement.setAttribute("id", scopeCreation.getId().toString());
        return descendentScopeElement;
    }

    private void buildStatesElement(Document document, Scope scope, Element scopeElement) {
        Element statesElement = document.createElement("states");
        scopeElement.appendChild(statesElement);

        for (State state : scope.getStates()) {
            Element stateElement = buildStateElement(document, state);
            statesElement.appendChild(stateElement);
        }
    }

    private Element buildStateElement(Document document, State state) {
        Element stateElement = document.createElement("state");
        stateElement.setAttribute("id", state.getId().toString());
        stateElement.setAttribute("parent-state", state.getParentState().getId().toString());

        Integer defaultStateDuration = stateService.getDefaultStateDuration(state);
        if (defaultStateDuration != null) {
            stateElement.setAttribute("default-duration", stateService.getDefaultStateDuration(state).toString());
        }

        if (!state.getStateActions().isEmpty()) {
            buildActionsElement(document, state, stateElement);
        }

        return stateElement;
    }

    private void buildActionsElement(Document document, State state, Element stateElement) {
        Element actionsElement = document.createElement("actions");
        stateElement.appendChild(actionsElement);

        for (StateAction stateAction : state.getStateActions()) {
            Element actionElement = buildActionElement(document, stateAction);
            actionsElement.appendChild(actionElement);
        }
    }

    private Element buildActionElement(Document document, StateAction stateAction) {
        Action action = stateAction.getAction();
        Element actionElement = document.createElement("action");
        actionElement.setAttribute("id", action.getId().toString());
        actionElement.setAttribute("type", action.getActionType().toString());
        actionElement.setAttribute("urgent", getXmlBoolean(stateAction.isRaisesUrgentFlag()));
        actionElement.setAttribute("default", getXmlBoolean(stateAction.isDefaultAction()));

        NotificationTemplate notificationTemplate = stateAction.getNotificationTemplate();
        if (notificationTemplate != null) {
            Element notificationTemplateElement = buildNotificationTemplateElement(document, notificationTemplate);
            actionElement.appendChild(notificationTemplateElement);
        }

        if (!stateAction.getStateActionAssignments().isEmpty()) {
            buildStateActionAssignmentsElement(document, stateAction, actionElement);
        }

        Element nextAppend = actionElement;
        PrismStateTransitionEvaluation stateTransitionEvaluation = stateService.getStateTransitionEvaluationByStateAction(stateAction);
        if (stateTransitionEvaluation != null) {
            Element stateTransitionEvaluationElement = buildStateTransitionEvaluationElement(document, actionElement, stateTransitionEvaluation);
            actionElement.appendChild(stateTransitionEvaluationElement);
            nextAppend = stateTransitionEvaluationElement;
        }

        if (!stateAction.getStateTransitions().isEmpty()) {
            buildStateTransitionsElement(document, stateAction, nextAppend);
        }

        if (!stateAction.getStateActionNotifications().isEmpty()) {
            buildStateActionNotificationsElement(document, stateAction, actionElement);
        }
        
        if (!action.getRedactions().isEmpty()) {
            buildActionRedactionsElement(document, action, actionElement);
        }

        return actionElement;
    }
    
    private Element buildNotificationTemplateElement(Document document, NotificationTemplate notificationTemplate) {
        Element notificationTemplateElement = document.createElement("task-notification");
        notificationTemplateElement.setAttribute("id", notificationTemplate.getId().toString());
        notificationTemplateElement.setAttribute("type", notificationTemplate.getNotificationType().toString());

        NotificationTemplate reminderNotificationTemplate = notificationTemplate.getReminderTemplate();
        if (reminderNotificationTemplate != null) {
            buildReminderNotificationTemplateElement(document, notificationTemplate, notificationTemplateElement, reminderNotificationTemplate);
        }

        return notificationTemplateElement;
    }
    
    private void buildReminderNotificationTemplateElement(Document document, NotificationTemplate notificationTemplate, Element notificationTemplateElement,
            NotificationTemplate reminderNotificationTemplate) {
        Element reminderNotificationTemplateElement = document.createElement("task-reminder");
        reminderNotificationTemplateElement.setAttribute("id", reminderNotificationTemplate.getId().toString());
        reminderNotificationTemplateElement.setAttribute("type", reminderNotificationTemplate.getNotificationType().toString());
        reminderNotificationTemplateElement.setAttribute("default-interval", notificationTemplateService.getDefaultReminderDuration(notificationTemplate)
                .toString());
        notificationTemplateElement.appendChild(reminderNotificationTemplateElement);
    }

    
    private void buildStateActionAssignmentsElement(Document document, StateAction stateAction, Element actionElement) {
        Element stateActionAssignmentsElement = document.createElement("roles");
        actionElement.appendChild(stateActionAssignmentsElement);

        for (StateActionAssignment stateActionAssignment : stateAction.getStateActionAssignments()) {
            Element roleElement = buildRoleElement(document, stateActionAssignment);
            stateActionAssignmentsElement.appendChild(roleElement);
        }
    }
    
    private Element buildRoleElement(Document document, StateActionAssignment stateActionAssignment) {
        Role role = stateActionAssignment.getRole();
        Element roleElement = document.createElement("role");
        roleElement.setAttribute("id", role.getId().toString());
        return roleElement;
    }
    
    private Element buildStateTransitionEvaluationElement(Document document, Element actionElement, PrismStateTransitionEvaluation stateTransitionEvaluation) {
        Element stateTransitionEvaluationElement = document.createElement("state-transition-evaluation");
        stateTransitionEvaluationElement.setAttribute("id", stateTransitionEvaluation.toString());
        actionElement.appendChild(stateTransitionEvaluationElement);
        return stateTransitionEvaluationElement;
    }

    private void buildStateTransitionsElement(Document document, StateAction stateAction, Element nextAppend) {
        Element transitionStatesElement = document.createElement("transition-states");
        nextAppend.appendChild(transitionStatesElement);

        for (StateTransition stateTransition : stateAction.getStateTransitions()) {
            Element stateTransitionElement = buildStateTransitionElement(document, stateTransition);
            transitionStatesElement.appendChild(stateTransitionElement);
        }
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
            buildRoleTransitionsElement(document, stateTransition, stateTransitionElement);
        }

        if (!stateTransition.getPropagatedActions().isEmpty()) {
            buildPropagatedActionsElement(document, stateTransition, stateTransitionElement);
        }

        return stateTransitionElement;
    }
    
    private void buildRoleTransitionsElement(Document document, StateTransition stateTransition, Element stateTransitionElement) {
        Element roleTransitionsElement = document.createElement("role-transitions");
        stateTransitionElement.appendChild(roleTransitionsElement);

        for (RoleTransition roleTransition : stateTransition.getRoleTransitions()) {
            Element roleTransitionElement = buildRoleTransitionElement(document, roleTransition);
            roleTransitionsElement.appendChild(roleTransitionElement);
        }
    }
    
    private Element buildRoleTransitionElement(Document document, RoleTransition roleTransition) {
        Element roleTransitionElement = document.createElement("role-transition");
        roleTransitionElement.setAttribute("id", roleTransition.getRole().getId().toString());
        roleTransitionElement.setAttribute("type", roleTransition.getRoleTransitionType().toString());
        roleTransitionElement.setAttribute("restrict-to-owner", getXmlBoolean(roleTransition.isRestrictToActionOwner()));

        Integer minimumPermittedTransitions = roleTransition.getMinimumPermitted();
        if (minimumPermittedTransitions != null) {
            roleTransitionElement.setAttribute("minimum", minimumPermittedTransitions.toString());
        }

        Integer maximumPermittedTransitions = roleTransition.getMaximumPermitted();
        if (maximumPermittedTransitions != null) {
            roleTransitionElement.setAttribute("maximum", maximumPermittedTransitions.toString());
        }

        Role transitionRole = roleTransition.getTransitionRole();
        Element transitionRoleElement = document.createElement("transition-role");
        transitionRoleElement.setAttribute("id", transitionRole.getId().toString());

        if (!transitionRole.getExcludedRoles().isEmpty()) {
            buildRoleTransitionExclusionsElement(document, transitionRole, transitionRoleElement);

        }

        roleTransitionElement.appendChild(transitionRoleElement);
        return roleTransitionElement;
    }

    private void buildRoleTransitionExclusionsElement(Document document, Role transitionRole, Element transitionRoleElement) {
        Element roleTransitionExclusionsElement = document.createElement("exclusions");
        transitionRoleElement.appendChild(roleTransitionExclusionsElement);

        for (Role excludedRole : transitionRole.getExcludedRoles()) {
            Element roleTransitionExclusionElement = buildRoleTransitionExclusionElement(document, excludedRole);
            roleTransitionExclusionsElement.appendChild(roleTransitionExclusionElement);
        }
    }
    
    private Element buildRoleTransitionExclusionElement(Document document, Role excludedRole) {
        Element roleTransitionExclusionElement = document.createElement("");
        roleTransitionExclusionElement.setAttribute("id", excludedRole.getId().toString());
        return roleTransitionExclusionElement;
    }
    
    private void buildPropagatedActionsElement(Document document, StateTransition stateTransition, Element stateTransitionElement) {
        Element propagatedActionsElement = document.createElement("propagated-actions");
        stateTransitionElement.appendChild(propagatedActionsElement);

        for (Action propagatedAction : stateTransition.getPropagatedActions()) {
            Element propagatedActionElement = buildPropagatedActionElement(document, propagatedAction);
            propagatedActionsElement.appendChild(propagatedActionElement);
        }
    }
    
    private Element buildPropagatedActionElement(Document document, Action propagatedAction) {
        Element propagatedActionElement = document.createElement("propagated-action");
        propagatedActionElement.setAttribute("id", propagatedAction.getId().toString());
        return propagatedActionElement;
    }
    
    private void buildStateActionNotificationsElement(Document document, StateAction stateAction, Element actionElement) {
        Element stateActionNotificationsElement = document.createElement("update-notifications");
        actionElement.appendChild(stateActionNotificationsElement);

        for (StateActionNotification stateActionNotification : stateAction.getStateActionNotifications()) {
            Element stateActionNotificationElement = buildStateActionNotificationElement(document, stateActionNotification);
            stateActionNotificationsElement.appendChild(stateActionNotificationElement);
        }
    }
    
    private Element buildStateActionNotificationElement(Document document, StateActionNotification stateActionNotification) {
        Element stateActionNotificationElement = document.createElement("update-notification");
        stateActionNotificationElement.setAttribute("id", stateActionNotification.getNotificationTemplate().getId().toString());
        stateActionNotificationElement.setAttribute("type", stateActionNotification.getNotificationTemplate().getNotificationType().toString());
        stateActionNotificationElement.setAttribute("role", stateActionNotification.getRole().getId().toString());
        return stateActionNotificationElement;
    }
    
    private void buildActionRedactionsElement(Document document, Action action, Element actionElement) {
        Element actionRedactionsElement = document.createElement("redactions");
        actionElement.appendChild(actionRedactionsElement);
        
        for (ActionRedaction actionRedaction : action.getRedactions()) {
            buildActionRedactionElement(document, actionRedactionsElement, actionRedaction);
        }
    }

    private void buildActionRedactionElement(Document document, Element visibilityExclusionsElement,
            ActionRedaction actionRedaction) {
        Element actionRedactionElement = document.createElement("redaction");
        actionRedactionElement.setAttribute("role", actionRedaction.getRole().getId().toString());
        actionRedactionElement.setAttribute("rule", actionRedaction.getRedactionType().toString());
        visibilityExclusionsElement.appendChild(actionRedactionElement);
    }

    private String getXmlBoolean(boolean javaBoolean) {
        return javaBoolean ? "true" : "false";
    }

}
