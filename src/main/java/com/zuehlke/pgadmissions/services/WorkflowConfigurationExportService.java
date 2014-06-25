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
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
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
    private NotificationService notificationService;

    public String exportWorkflowConfiguration() {
        DocumentBuilder documentBuilder = prepareDocumentBuilder();
        Document document = documentBuilder.newDocument();
        document.setXmlVersion("1.0");

        Element scopesElement = document.createElement("scopes");
        document.appendChild(scopesElement);

        for (Scope scopes : systemService.getScopes()) {
            Element resourceElement = buildScopeElement(document, scopes);
            scopesElement.appendChild(resourceElement);
        }

        return parseDocumentToString(document).trim();
    }

    private DocumentBuilder prepareDocumentBuilder() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    private String parseDocumentToString(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }

    private Element buildScopeElement(Document document, Scope scope) {
        Element scopeElement = document.createElement("scope");
        scopeElement.setAttribute("id", scope.getId().toString());

        if (!scope.getStates().isEmpty()) {
            buildStatesElement(document, scope, scopeElement);
        }

        return scopeElement;
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

        Integer defaultStateDuration = systemService.getStateDuration(state);
        if (defaultStateDuration != null) {
            stateElement.setAttribute("default-duration", defaultStateDuration.toString());
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
        actionElement.setAttribute("urgent", getXmlBoolean(stateAction.isRaisesUrgentFlag()));
        actionElement.setAttribute("default", getXmlBoolean(stateAction.isDefaultAction()));

        NotificationTemplate notificationTemplate = stateAction.getNotificationTemplate();
        if (notificationTemplate != null) {
            actionElement.setAttribute("notification", notificationTemplate.getId().toString());
            actionElement.setAttribute("reminder-interval", notificationService.getReminderDuration(systemService.getSystem(), notificationTemplate).toString());
        }

        if (!stateAction.getStateActionAssignments().isEmpty()) {
            buildStateActionAssignmentsElement(document, stateAction, actionElement);
        }

        if (!stateAction.getStateTransitions().isEmpty()) {
            buildStateTransitionsElement(document, stateAction, actionElement);
        }

        if (!stateAction.getStateActionNotifications().isEmpty()) {
            buildStateActionNotificationsElement(document, stateAction, actionElement);
        }
        
        if (!action.getRedactions().isEmpty()) {
            buildActionRedactionsElement(document, action, actionElement);
        }

        return actionElement;
    }

    private void buildStateActionAssignmentsElement(Document document, StateAction stateAction, Element actionElement) {
        Element stateActionAssignmentsElement = document.createElement("roles");
        actionElement.appendChild(stateActionAssignmentsElement);

        for (StateActionAssignment stateActionAssignment : stateAction.getStateActionAssignments()) {
            Element stateActionAssignmentElement = buildStateActionAssignmentElement(document, stateActionAssignment);
            stateActionAssignmentsElement.appendChild(stateActionAssignmentElement);
        }
    }

    private Element buildStateActionAssignmentElement(Document document, StateActionAssignment stateActionAssignment) {
        Element stateActionAssignmentElement = document.createElement("role");
        stateActionAssignmentElement.setAttribute("id", stateActionAssignment.getRole().getId().toString());

        if (!stateActionAssignment.getEnhancements().isEmpty()) {
            buildStateActionEnhancementsElement(document, stateActionAssignment, stateActionAssignmentElement);
        }

        return stateActionAssignmentElement;
    }

    private void buildStateActionEnhancementsElement(Document document, StateActionAssignment stateActionAssignment, Element stateActionAssignmentElement) {
        Element stateActionEnhancementsElement = document.createElement("enhancements");
        stateActionAssignmentElement.appendChild(stateActionEnhancementsElement);

        for (StateActionEnhancement stateActionEnhancement : stateActionAssignment.getEnhancements()) {
            Element stateActionEnhancementElement = buildStateActionEnhancementElement(document, stateActionEnhancement);
            stateActionEnhancementsElement.appendChild(stateActionEnhancementElement);
        }
    }

    private Element buildStateActionEnhancementElement(Document document, StateActionEnhancement stateActionEnhancement) {
        Element stateActionEnhancementElement = document.createElement("enhancement");
        stateActionEnhancementElement.setAttribute("type", stateActionEnhancement.getEnhancementType().toString());

        Action delegatedAction = stateActionEnhancement.getDelegatedAction();
        if (delegatedAction != null) {
            stateActionEnhancementElement.setAttribute("delegated-action", delegatedAction.getId().toString());
        }

        return stateActionEnhancementElement;
    }

    private void buildStateTransitionsElement(Document document, StateAction stateAction, Element actionElement) {
        Element transitionStatesElement = document.createElement("transition-states");
        
        PrismStateTransitionEvaluation stateTransitionEvaluation = stateService.getTransitionEvaluation(stateAction);
        if (stateTransitionEvaluation != null) {
            transitionStatesElement.setAttribute("evaluation", stateTransitionEvaluation.toString());
        }
        
        actionElement.appendChild(transitionStatesElement);

        for (StateTransition stateTransition : stateAction.getStateTransitions()) {
            Element stateTransitionElement = buildStateTransitionElement(document, stateTransition);
            transitionStatesElement.appendChild(stateTransitionElement);
        }
    }

    private Element buildStateTransitionElement(Document document, StateTransition stateTransition) {
        Element stateTransitionElement = document.createElement("transition-state");
        stateTransitionElement.setAttribute("id", stateTransition.getTransitionState().getId().toString());
        stateTransitionElement.setAttribute("action", stateTransition.getTransitionAction().getId().toString());
        stateTransitionElement.setAttribute("comment", getXmlBoolean(stateTransition.isDoPostComment()));

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
        Element roleTransitionExclusionElement = document.createElement("exclusion");
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
        stateActionNotificationElement.setAttribute("role", stateActionNotification.getRole().getId().toString());
        return stateActionNotificationElement;
    }

    private void buildActionRedactionsElement(Document document, Action action, Element actionElement) {
        Element actionRedactionsElement = document.createElement("redactions");
        actionElement.appendChild(actionRedactionsElement);

        for (ActionRedaction actionRedaction : action.getRedactions()) {
            Element actionRedactionElement = buildActionRedactionElement(document, actionRedaction);
            actionRedactionsElement.appendChild(actionRedactionElement);
        }
    }

    private Element buildActionRedactionElement(Document document, ActionRedaction actionRedaction) {
        Element actionRedactionElement = document.createElement("redaction");
        actionRedactionElement.setAttribute("role", actionRedaction.getRole().getId().toString());
        actionRedactionElement.setAttribute("type", actionRedaction.getRedactionType().toString());
        return actionRedactionElement;
    }

    private String getXmlBoolean(boolean javaBoolean) {
        return javaBoolean ? "true" : "false";
    }

}
