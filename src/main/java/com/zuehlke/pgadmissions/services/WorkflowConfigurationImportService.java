package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionEnhancementType;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;

@Service
@Transactional(timeout = 60)
public class WorkflowConfigurationImportService {
    
    private final static String WORKFLOW_CONFIGURATION_XSD = "xml/workflow_configuration_schema.xsd";
    
    private WorkflowGraph workflowGraph = new WorkflowGraph();

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
            stateService.disableStateActionAssignments();
            stateService.disableStateActionEnhancements();
            stateService.disableStateTransitions();

            importScopes(document);
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
    private void importStates(Element scopeElement, PrismScope scopeId) throws WorkflowConfigurationException {
        NodeList stateElements = scopeElement.getElementsByTagName("state");

        for (int i = 0; i < stateElements.getLength(); i++) {
            Element stateElement = (Element) stateElements.item(i);
            String stateElementId = stateElement.getAttribute("id");

            PrismState stateId = getValueOf(PrismState.class, stateElementId);

            if (stateId.getScope() != scopeId) {
                throw new WorkflowConfigurationException(stateId, scopeId);
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

            NotificationTemplate notificationTemplate = null;
            PrismNotificationTemplate notificationTemplateId = null;
            Integer reminderInterval = null;
            String actionElementNotification = actionElement.getAttribute("notification");
            if (actionElementNotification != null) {
                notificationTemplateId = getValueOf(PrismNotificationTemplate.class, actionElementNotification);

                if (actionId.getScope() != notificationTemplateId.getScope()) {
                    throw new WorkflowConfigurationException(notificationTemplateId, actionId);
                } else if (notificationTemplateId.getNotificationPurpose() != PrismNotificationPurpose.REQUEST) {
                    throw new WorkflowConfigurationException(notificationTemplateId, PrismNotificationPurpose.REQUEST);
                }

                String actionReminderInterval = actionElement.getAttribute("reminder-interval");
                if (actionReminderInterval != null) {
                    reminderInterval = Integer.parseInt(actionReminderInterval);

                    if (reminderInterval < 1) {
                        throw new WorkflowConfigurationException(reminderInterval);
                    }
                }

                notificationTemplate = entityService.getByProperty(NotificationTemplate.class, "id", notificationTemplateId);
            }

            State state = entityService.getByProperty(State.class, "id", stateId);
            Action action = entityService.getByProperty(Action.class, "id", actionId);
            boolean raisesUrgentFlag = actionElement.getAttribute("urgent") == "true";
            boolean defaultAction = actionElement.getAttribute("default") == "true";

            StateAction transientStateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(raisesUrgentFlag)
                    .withDefaultAction(defaultAction).withNotificationTemplate(notificationTemplate).withEnabled(true);
            StateAction stateAction = entityService.createOrUpdate(transientStateAction);

            if (reminderInterval != null) {
                NotificationConfiguration configuration = notificationService.getConfiguration(systemService.getSystem(), notificationTemplate);
                configuration.setReminderInterval(reminderInterval);
            }

            importRoles(actionElement, stateAction);
            importStateTransitions(actionElement, stateAction);
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

            PrismActionEnhancementType enhancementType = getValueOf(PrismActionEnhancementType.class, enhancementElementType);

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

                // TODO: propagated actions
//                String feedback = importPropagatedActions(stateTransitionElement, stateTransition);

                State transitionState = entityService.getByProperty(State.class, "id", transitionStateId);
                Action transitionAction = entityService.getByProperty(Action.class, "id", transitionActionId);
                boolean doPostComment = stateTransitionElement.getAttribute("comment") == "true";

                StateTransition transientStateTransition = new StateTransition().withStateAction(stateAction).withTransitionState(transitionState)
                        .withTransitionAction(transitionAction).withStateTransitionEvaluation(stateTransitionEvaluation).withDoPostComment(doPostComment)
                        .withEnabled(true);
                entityService.createOrUpdate(transientStateTransition);
                
                workflowGraph.createOrUpdateNode(stateAction.getState().getId(), transitionStateId, stateTransitionEvaluation);
            }
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
            WorkflowNode node = nodes.get(stateId);
            WorkflowNode inverseNode = nodes.get(transitionStateId);
            
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
        
        private class WorkflowNode {

            private HashMap<PrismState, PrismStateTransitionEvaluation> incomingEdges = Maps.newHashMap();

            private HashMap<PrismState, PrismStateTransitionEvaluation> outgoingEdges = Maps.newHashMap();

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

            public boolean isEntryPoint() {
                return incomingEdges.isEmpty();
            }

            public boolean isExitPoint() {
                return outgoingEdges.isEmpty();
            }

        }
        
    }

}
