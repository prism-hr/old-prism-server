package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismEnhancementType;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRedactionType;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;

@Service
@Transactional(timeout = 60)
public class WorkflowConfigurationImportService {

    private final static String WORKFLOW_CONFIGURATION_XSD = "xml/workflow_configuration_schema.xsd";

    private final WorkflowGraph workflowGraph = new WorkflowGraph();
    
    private final Set<State> configuredStates = Sets.newHashSet();
    
    private final Set<NotificationTemplate> configuredTemplates = Sets.newHashSet();

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SystemService systemService;

    // TODO: Test invalid XML
    // TODO: Test malformed XML
    public void importWorkflowConfiguration(String configuration) throws WorkflowConfigurationException {
        try {
            DocumentBuilder documentBuilder = prepareDocumentBuilder();
            Document document = documentBuilder.parse(configuration);

            stateService.disableStateActions();
            notificationService.disableConfigurations();
            stateService.disableStateActionAssignments();
            stateService.disableStateActionEnhancements();
            stateService.disableStateTransitions();
            stateService.disableStateDurations();

            importScopes(document);
            
            for (State state : configuredStates) {
                stateService.enableStateDurations(state);
            }
            
            for (NotificationTemplate template : configuredTemplates) {
                notificationService.enableConfigurations(template);
            }
        } catch (IOException e) {
            throw new WorkflowConfigurationException(e);
        } catch (SAXException e) {
            throw new WorkflowConfigurationException(e);
        }
    }

    private DocumentBuilder prepareDocumentBuilder() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            documentFactory.setSchema(prepareValidationSchema());
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    private Schema prepareValidationSchema() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            return schemaFactory.newSchema(new File(WORKFLOW_CONFIGURATION_XSD));
        } catch (SAXException e) {
            throw new Error(e);
        }
    }

    // TODO: Test invalid scope
    // TODO: Test no creation action for scope
    private void importScopes(Document document) throws WorkflowConfigurationException {
        NodeList scopeElements = document.getElementsByTagName("scope");

        for (int i = 0; i < scopeElements.getLength(); i++) {
            Element scopeElement = (Element) scopeElements.item(i);
            String scopeElementId = scopeElement.getAttribute("id");

            PrismScope scopeId = getValueOf(PrismScope.class, scopeElementId);
            importStates(scopeElement, scopeId);
        }
    }

    // TODO: Test invalid state
    // TODO: Test invalid state scope
    // TODO: Test invalid state duration
    private void importStates(Element scopeElement, PrismScope scopeId) throws WorkflowConfigurationException {
        NodeList stateElements = scopeElement.getElementsByTagName("state");

        for (int i = 0; i < stateElements.getLength(); i++) {
            Element stateElement = (Element) stateElements.item(i);
            String stateElementId = stateElement.getAttribute("id");

            PrismState stateId = getValueOf(PrismState.class, stateElementId);

            if (stateId.getScope() != scopeId) {
                throw new WorkflowConfigurationException(stateId, scopeId);
            }

            String stateElementDuration = stateElement.getAttribute("duration");
            
            if (stateElementDuration != null) {
                Integer stateDuration = Integer.parseInt(stateElementDuration);
                
                if (stateDuration < 1) {
                    throw new WorkflowConfigurationException(stateDuration);
                }
                
                State state = entityService.getByProperty(State.class, "id", stateId);
                
                StateDuration transientStateDuration = new StateDuration().withSystem(systemService.getSystem()).withState(state).withDuration(stateDuration);
                entityService.createOrUpdate(transientStateDuration);
                
                configuredStates.add(state);
            }
            
            importActions(stateElement, stateId);
        }
    }

    // TODO: Test invalid action
    // TODO: Test invalid action scope
    // TODO: Test invalid notification template
    // TODO: Test invalid notification template scope
    // TODO: Test invalid notification template type
    // TODO: Test importing new version of workflow
    private void importActions(Element stateElement, PrismState stateId) throws WorkflowConfigurationException {
        NodeList actionElements = stateElement.getElementsByTagName("action");

        for (int i = 0; i < actionElements.getLength(); i++) {
            Element actionElement = (Element) actionElements.item(i);
            String actionElementId = actionElement.getAttribute("id");

            PrismAction actionId = getValueOf(PrismAction.class, actionElementId);

            if (stateId.getScope() != actionId.getScope()) {
                throw new WorkflowConfigurationException(actionId, stateId);
            }

            NotificationTemplate template = null;
            PrismNotificationTemplate templateId = null;
            Integer reminderInterval = null;
            String actionElementNotification = actionElement.getAttribute("notification");
            if (actionElementNotification != null) {
                templateId = getValueOf(PrismNotificationTemplate.class, actionElementNotification);

                if (actionId.getScope() != templateId.getScope()) {
                    throw new WorkflowConfigurationException(templateId, actionId);
                } else if (templateId.getNotificationPurpose() != PrismNotificationPurpose.REQUEST) {
                    throw new WorkflowConfigurationException(templateId, PrismNotificationPurpose.REQUEST);
                }

                String actionReminderInterval = actionElement.getAttribute("interval");
                if (actionReminderInterval != null) {
                    reminderInterval = Integer.parseInt(actionReminderInterval);

                    if (reminderInterval < 1) {
                        throw new WorkflowConfigurationException(reminderInterval);
                    }
                }

                template = entityService.getByProperty(NotificationTemplate.class, "id", templateId);
                
                configuredTemplates.add(template);
            }

            State state = entityService.getByProperty(State.class, "id", stateId);
            Action action = entityService.getByProperty(Action.class, "id", actionId);
            boolean raisesUrgentFlag = actionElement.getAttribute("urgent") == "true";
            boolean defaultAction = actionElement.getAttribute("default") == "true";

            StateAction transientStateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(raisesUrgentFlag)
                    .withDefaultAction(defaultAction).withNotificationTemplate(template).withEnabled(true);
            StateAction stateAction = entityService.createOrUpdate(transientStateAction);

            if (reminderInterval != null) {
                NotificationConfiguration configuration = notificationService.getConfiguration(systemService.getSystem(), template);
                configuration.setReminderInterval(reminderInterval);
            }

            importRoles(actionElement, stateAction);
            importStateTransitions(actionElement, stateAction);
            importRedactions(actionElement, stateAction);
            importNotifications(actionElement, stateAction);
        }
    }

    // TODO: Test invalid action assignment
    // TODO: Test invalid action assignment scope
    private void importRoles(Element actionElement, StateAction stateAction) throws WorkflowConfigurationException {
        PrismAction actionId = stateAction.getAction().getId();
        NodeList roleElements = actionElement.getElementsByTagName("role");

        for (int i = 0; i < roleElements.getLength(); i++) {
            Element roleElement = (Element) roleElements.item(i);
            String roleElementId = roleElement.getAttribute("id");

            PrismRole roleId = getValueOf(PrismRole.class, roleElementId);

            if (roleId.getScope().getPrecedence() < actionId.getScope().getPrecedence()) {
                throw new WorkflowConfigurationException(roleId, actionId);
            }

            Role role = entityService.getByProperty(Role.class, "id", roleId);

            StateActionAssignment transientStateActionAssignment = new StateActionAssignment().withStateAction(stateAction).withRole(role).withEnabled(true);
            StateActionAssignment stateActionAssignment = entityService.createOrUpdate(transientStateActionAssignment);

            importEnhancements(roleElement, stateActionAssignment);
        }
    }

    // TODO: Test invalid action assignment enhancement
    // TODO: Test invalid action assignment enhancement scope
    // TODO: Test invalid delegate action
    // TODO: Test invalid delegate action scope
    private void importEnhancements(Element roleElement, StateActionAssignment stateActionAssignment) throws WorkflowConfigurationException {
        PrismAction actionId = stateActionAssignment.getStateAction().getAction().getId();
        NodeList enhancementElements = roleElement.getElementsByTagName("enhancement");

        for (int i = 0; i < enhancementElements.getLength(); i++) {
            Element enhancementElement = (Element) enhancementElements.item(i);
            String enhancementElementType = enhancementElement.getAttribute("type");

            PrismEnhancementType enhancementType = getValueOf(PrismEnhancementType.class, enhancementElementType);

            if (enhancementType.getScope() != actionId.getScope()) {
                throw new WorkflowConfigurationException(enhancementType, actionId);
            }

            Action delegatedAction = null;
            PrismAction delegatedActionId = null;
            String enhancementElementDelegatedAction = enhancementElement.getAttribute("delegated-action");
            if (enhancementElementDelegatedAction != null) {
                delegatedActionId = getValueOf(PrismAction.class, enhancementElementDelegatedAction);

                if (delegatedActionId.getScope() != actionId.getScope()) {
                    throw new WorkflowConfigurationException(delegatedActionId, actionId);
                }

                delegatedAction = entityService.getByProperty(Action.class, "id", actionId);
            }

            StateActionEnhancement transientStateActionEnhancement = new StateActionEnhancement().withStateActionAssignment(stateActionAssignment)
                    .withEnhancementType(enhancementType).withDelegatedAction(delegatedAction).withEnabled(true);
            entityService.createOrUpdate(transientStateActionEnhancement);

        }
    }

    // TODO: Test invalid state transition evaluation
    // TODO: Test invalid state transition evaluation assignment
    // TODO: Test invalid transition state
    // TODO: Test invalid transition state assignment
    // TODO: Test invalid transition action
    // TODO: Test invalid transition action assignment
    // TODO: Test invalid state transition (ambiguous)
    private void importStateTransitions(Element actionElement, StateAction stateAction) throws WorkflowConfigurationException {
        PrismState stateId = stateAction.getState().getId();
        PrismAction actionId = stateAction.getAction().getId();
        NodeList stateTransitionsElements = actionElement.getElementsByTagName("transition-states");

        if (stateTransitionsElements.getLength() > 1) {
            PrismStateTransitionEvaluation stateTransitionEvaluation = null;

            Element stateTransitionsElement = (Element) stateTransitionsElements.item(0);
            String stateTransitionsElementEvaluation = stateTransitionsElement.getAttribute("evaluation");

            if (stateTransitionsElementEvaluation != null) {
                stateTransitionEvaluation = getValueOf(PrismStateTransitionEvaluation.class, stateTransitionsElementEvaluation);

                if (!stateTransitionEvaluation.getInvokingActions().contains(actionId)) {
                    throw new WorkflowConfigurationException(stateTransitionEvaluation, actionId);
                }
            }

            NodeList stateTransitionElements = stateTransitionsElement.getElementsByTagName("transition-state");

            for (int i = 0; i < stateTransitionElements.getLength(); i++) {
                Element stateTransitionElement = (Element) stateTransitionElements.item(i);
                String stateTransitionElementStateId = stateTransitionElement.getAttribute("id");
                String stateTransitionElementActionId = stateTransitionElement.getAttribute("action");

                PrismState transitionStateId = getValueOf(PrismState.class, stateTransitionElementStateId);
                boolean isCreationTransition = actionService.isCreationAction(stateId, transitionStateId, actionId);

                if (transitionStateId.getScope() != actionId.getScope() && !isCreationTransition) {
                    throw new WorkflowConfigurationException(transitionStateId, stateId);
                }

                PrismAction transitionActionId = getValueOf(PrismAction.class, stateTransitionElementActionId);

                if (transitionActionId.getScope() != actionId.getScope() && !isCreationTransition) {
                    throw new WorkflowConfigurationException(transitionActionId, actionId);
                } else if (i > 0 && stateTransitionEvaluation == null) {
                    throw new WorkflowConfigurationException(stateId);
                }

                State transitionState = entityService.getByProperty(State.class, "id", transitionStateId);
                Action transitionAction = entityService.getByProperty(Action.class, "id", transitionActionId);
                boolean doPostComment = stateTransitionElement.getAttribute("comment") == "true";

                StateTransition transientStateTransition = new StateTransition().withStateAction(stateAction).withTransitionState(transitionState)
                        .withTransitionAction(transitionAction).withStateTransitionEvaluation(stateTransitionEvaluation).withDoPostComment(doPostComment)
                        .withEnabled(true);
                StateTransition stateTransition = entityService.createOrUpdate(transientStateTransition);

                workflowGraph.createOrUpdateNode(stateAction.getState().getId(), transitionStateId, stateTransitionEvaluation);

                importPropagatedActions(stateTransitionElement, stateTransition);
                importRoleTransitions(stateTransitionElement, stateTransition);
            }
        }
    }

    // TODO: test invalid redaction role
    // TODO: test invalid redaction type
    private void importRedactions(Element actionElement, StateAction stateAction) throws WorkflowConfigurationException {
        NodeList redactionElements = actionElement.getElementsByTagName("redaction");

        for (int i = 0; i < redactionElements.getLength(); i++) {
            Element redactionElement = (Element) redactionElements.item(i);

            String redactionElementRole = redactionElement.getAttribute("role");
            PrismRole redactionRoleId = getValueOf(PrismRole.class, redactionElementRole);

            String redactionElementType = redactionElement.getAttribute("type");
            PrismRedactionType redactionType = getValueOf(PrismRedactionType.class, redactionElementType);

            Role role = entityService.getByProperty(Role.class, "id", redactionRoleId);

            ActionRedaction transientActionRedaction = new ActionRedaction().withAction(stateAction.getAction()).withRole(role)
                    .withRedactionType(redactionType);
            entityService.createOrUpdate(transientActionRedaction);
        }
    }
    
    // TODO: Test invalid notification template
    // TODO: Test invalid notification template assignment
    // TODO: Test invalid notification template purpose
    // TODO: Test invalid role
    private void importNotifications(Element actionElement, StateAction stateAction) throws WorkflowConfigurationException {
        PrismAction actionId = stateAction.getAction().getId();
        NodeList notificationElements = actionElement.getElementsByTagName("notification");

        for (int i = 0; i < notificationElements.getLength(); i++) {
            Element notificationElement = (Element) notificationElements.item(i);

            String notificationElementId = notificationElement.getAttribute("id");
            PrismNotificationTemplate templateId = getValueOf(PrismNotificationTemplate.class, notificationElementId);

            if (templateId.getScope() != actionId.getScope()) {
                throw new WorkflowConfigurationException(templateId, actionId);
            } else if (templateId.getNotificationPurpose() != PrismNotificationPurpose.UPDATE) {
                throw new WorkflowConfigurationException(templateId, PrismNotificationPurpose.UPDATE);
            }

            String notificationElementRole = notificationElement.getAttribute("role");
            PrismRole roleId = getValueOf(PrismRole.class, notificationElementRole);

            Role role = entityService.getByProperty(Role.class, "id", roleId);
            NotificationTemplate template = entityService.getByProperty(NotificationTemplate.class, "id", templateId);

            StateActionNotification transientStateActionNotification = new StateActionNotification().withStateAction(stateAction).withRole(role)
                    .withNotificationTemplate(template);
            entityService.createOrUpdate(transientStateActionNotification);
            
            configuredTemplates.add(template);
        }

    }

    // TODO: Test invalid propagated action
    // TODO: Test invalid propagated action assignment
    private void importPropagatedActions(Element stateTransitionElement, StateTransition stateTransition) throws WorkflowConfigurationException {
        NodeList propagatedActionElements = stateTransitionElement.getElementsByTagName("propagated-action");

        for (int i = 0; i < propagatedActionElements.getLength(); i++) {
            Element propagatedActionElement = (Element) propagatedActionElements.item(i);

            String propagatedActionElementId = propagatedActionElement.getAttribute("id");
            PrismAction propagatedActionId = getValueOf(PrismAction.class, propagatedActionElementId);

            Action propagatedAction = entityService.getByProperty(Action.class, "id", propagatedActionId);
            stateTransition.getPropagatedActions().add(propagatedAction);

            workflowGraph.attachPropagatedActionToNode(stateTransition.getStateAction().getState().getId(), propagatedActionId);
        }
    }

    private void importRoleTransitions(Element stateTransitionElement, StateTransition stateTransition) throws WorkflowConfigurationException {
        NodeList roleTransitionElements = stateTransitionElement.getElementsByTagName("role-transition");
        
        for (int i = 0; i < roleTransitionElements.getLength(); i++) {
            Element roleTransitionElement = (Element) roleTransitionElements.item(i);
        }

    }

    private <T extends Enum<T>> T getValueOf(Class<T> clazz, String value) throws WorkflowConfigurationException {
        try {
            return Enum.valueOf(clazz, value);
        } catch (IllegalArgumentException e) {
            throw new WorkflowConfigurationException(clazz, value);
        }
    }

    private class WorkflowGraph {

        private final HashMap<PrismState, WorkflowNode> nodes = Maps.newHashMap();

        private final List<PrismState> entryNodes = Lists.newArrayList();

        private final List<PrismState> exitNodes = Lists.newArrayList();

        public void createOrUpdateNode(PrismState stateId, PrismState transitionStateId, PrismStateTransitionEvaluation stateTransitionEvaluation) {
            WorkflowNode node = getWorkflowNode(stateId);
            WorkflowNode inverseNode = getWorkflowNode(transitionStateId);

            if (node == null) {
                node = nodes.put(stateId, new WorkflowNode());
            }

            if (inverseNode == null) {
                inverseNode = nodes.put(transitionStateId, new WorkflowNode());
            }

            node.addOutgoingEdge(stateId, stateTransitionEvaluation);
            inverseNode.addIncomingEdge(stateId, stateTransitionEvaluation);

            if (node.isEntryPoint() && !entryNodes.contains(stateId)) {
                entryNodes.add(stateId);
            }

            if (node.isExitPoint() && !exitNodes.contains(stateId)) {
                exitNodes.add(stateId);
            }
        }

        public void attachPropagatedActionToNode(PrismState stateId, PrismAction propagatedActionId) {
            WorkflowNode node = getWorkflowNode(stateId);
            node.addPropagatedAction(propagatedActionId);
        }

        public WorkflowNode getWorkflowNode(PrismState stateId) {
            return nodes.get(stateId);
        }

        private class WorkflowNode {

            private HashMap<PrismState, PrismStateTransitionEvaluation> incomingEdges = Maps.newHashMap();

            private HashMap<PrismState, PrismStateTransitionEvaluation> outgoingEdges = Maps.newHashMap();

            private List<PrismAction> propagatedActions = Lists.newArrayList();

            public HashMap<PrismState, PrismStateTransitionEvaluation> getIncomingEdges() {
                return incomingEdges;
            }

            public void addIncomingEdge(PrismState transitionStateId, PrismStateTransitionEvaluation transitionEvaluation) {
                incomingEdges.put(transitionStateId, transitionEvaluation);
            }

            public HashMap<PrismState, PrismStateTransitionEvaluation> getOutgoingEdges() {
                return outgoingEdges;
            }

            public void addOutgoingEdge(PrismState transitionStateId, PrismStateTransitionEvaluation transitionEvaluation) {
                outgoingEdges.put(transitionStateId, transitionEvaluation);
            }

            public List<PrismAction> getPropagatedActions() {
                return propagatedActions;
            }

            public void addPropagatedAction(PrismAction propagatedActionId) {
                if (!propagatedActions.contains(propagatedActionId)) {
                    propagatedActions.add(propagatedActionId);
                }
            }

            public boolean isEntryPoint() {
                return incomingEdges.isEmpty();
            }

            public boolean isExitPoint() {
                return outgoingEdges.isEmpty();
            }

        }

    }

}
