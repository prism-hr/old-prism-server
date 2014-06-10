package com.zuehlke.pgadmissions.services;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map.Entry;

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
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;

@Service
@Transactional
public class WorkflowConfigurationService {

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

        return parseDocumentToString(document);
    }

    public void importWorkflowConfiguration(String configuration) throws Exception {
        DocumentBuilder documentBuilder = prepareDocumentBuilder();
        Document document = documentBuilder.parse(configuration);

        NodeList scopeElements = document.getElementsByTagName("scope");
        for (int i = 0; i < scopeElements.getLength(); i++) {
            Element scopeElement = (Element) scopeElements.item(i);
            importScopeDefinition(scopeElement);
        }
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
        stateElement.setAttribute("parent-state", state.getParentState().getId().toString());

        Integer defaultStateDuration = stateService.getDefaultStateDuration(state);
        if (defaultStateDuration != null) {
            stateElement.setAttribute("default-duration", stateService.getDefaultStateDuration(state).toString());
        }

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
            Element transitionStatesElement = document.createElement("transition-states");
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
        if (reminderNotificationTemplate != null) {
            Element reminderNotificationTemplateElement = document.createElement("task-reminder");
            reminderNotificationTemplateElement.setAttribute("id", reminderNotificationTemplate.getId().toString());
            reminderNotificationTemplateElement.setAttribute("type", reminderNotificationTemplate.getNotificationType().toString());
            reminderNotificationTemplateElement.setAttribute("default-interval", notificationTemplateService.getDefaultReminderDuration(notificationTemplate)
                    .toString());
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

    private void importScopeDefinition(Element scopeElement) {
        Scope scope = new Scope(PrismScope.valueOf(scopeElement.getAttribute("id")), Integer.parseInt(scopeElement.getAttribute("precedence")));
        entityService.save(scope);

        NodeList stateElements = scopeElement.getElementsByTagName("state");
        for (int i = 0; i < stateElements.getLength(); i++) {
            Element stateElement = (Element) stateElements.item(i);
            importStateDefinition(scope, stateElement);
        }
    }

    private void importStateDefinition(Scope scope, Element stateElement) {
        State parentState = (State) entityService.getOrCreate(new State(PrismState.valueOf(stateElement.getAttribute("parent-state")), scope));
        parentState.setParentState(parentState);

        State state = (State) entityService.getOrCreate(new State(PrismState.valueOf(stateElement.getAttribute("id")), scope));
        state.setParentState(parentState);

        String defaultStateDurationDefinition = stateElement.getAttribute("default-duration");
        if (defaultStateDurationDefinition != null) {
            importStateDuration(state, defaultStateDurationDefinition);
        }

        NodeList actionElements = stateElement.getElementsByTagName("action");
        HashMap<Element, Action> actionInserts = Maps.newHashMap();
        for (int i = 0; i < actionElements.getLength(); i++) {
            Element actionElement = (Element) actionElements.item(i);
            importActionDefinition(actionElement, scope, actionInserts);
        }

        for (Entry<Element, Action> actionInsert : actionInserts.entrySet()) {
            importDelegateActionDefinition(actionInsert.getKey(), actionInsert.getValue());
        }
    }

    private void importStateDuration(State state, String defaultStateDurationDefinition) {
        StateDuration stateDuration = new StateDuration(systemService.getSystem(), state, Integer.parseInt(defaultStateDurationDefinition));
        entityService.save(stateDuration);
    }

    private void importActionDefinition(Element actionElement, Scope scope, HashMap<Element, Action> actionInserts) {
        Action action = (Action) entityService.getOrCreate(new Action(PrismAction.valueOf(actionElement.getAttribute("id")), PrismActionType
                .valueOf(actionElement.getAttribute("type")), scope));
        if (actionElement.hasAttribute("delegate-action")) {
            actionInserts.put(actionElement, action);
        }

        NodeList roleElements = actionElement.getElementsByTagName("role");
        for (int i = 0; i < roleElements.getLength(); i++) {
            Element roleElement = (Element) roleElements.item(i);
            entityService.getOrCreate(new Role(PrismRole.valueOf(roleElement.getAttribute("id")), scope));
        }

        NodeList taskNotificationElements = actionElement.getElementsByTagName("task-notification");
        if (taskNotificationElements.getLength() == 1) {
            Element taskNotificationElement = (Element) taskNotificationElements.item(0);
            importTaskNotificationDefinition(taskNotificationElement);
        }
    }

    private void importTaskNotificationDefinition(Element taskNotificationElement) {
        NotificationTemplate taskNotification = (NotificationTemplate) entityService.getOrCreate(new NotificationTemplate(PrismNotificationTemplate
                .valueOf(taskNotificationElement.getAttribute("id"))));

        NodeList taskReminderElements = taskNotificationElement.getElementsByTagName("task-reminder");
        if (taskReminderElements.getLength() == 1) {
            Element taskReminderElement = (Element) taskReminderElements.item(0);
            NotificationTemplate taskReminder = (NotificationTemplate) entityService.getOrCreate(new NotificationTemplate(PrismNotificationTemplate
                    .valueOf(taskReminderElement.getAttribute("id"))));
            taskNotification.setReminderTemplate(taskReminder);
        }
    }

    private void importDelegateActionDefinition(Element actionElement, Action action) {
        Action delegateAction = entityService.getByProperty(Action.class, "id", PrismAction.valueOf(actionElement.getAttribute("delegate-action")));
        action.setDelegateAction(delegateAction);
    }

    private String getXmlBoolean(boolean javaBoolean) {
        return javaBoolean ? "true" : "false";
    }

}
