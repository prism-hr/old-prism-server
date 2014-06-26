package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
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

@Service
@Transactional(timeout = 60)
public class WorkflowConfigurationImportService {

    private final static String WORKFLOW_CONFIGURATION_XSD = "xml/workflow_configuration_schema.xsd";

    private static final String IMPORT_SUCCESS = "Your workflow configuration has been applied successfully";

    private static final String IMPORT_FAILURE_XML_INVALID = "Your workflow configuration is not compliant with our schema";

    private static final String IMPORT_FAILURE_XML_MALFORMED = "Your workflow configuration is malformed";

    private static final String IMPORT_INVALID_ENTITY = "is not a not a valid";

    private static final String IMPORT_INVALID_ENTITY_ASSIGNMENT = "for the";

    private static final String IMPORT_INVALID_NOTIFICATION = "template";

    private static final String IMPORT_INVALID_REMINDER_INTERVAL = "reminder interval";

    private static final String IMPORT_INVALID_STATE_TRANSITION = "has no unambiguous transition state";
    
    private WorkflowGraph workflowGraph = new WorkflowGraph();

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
    public SimpleEntry<Boolean, String> importWorkflowConfiguration(String configuration) {
        try {
            DocumentBuilder documentBuilder = prepareDocumentBuilder();
            Document document = documentBuilder.parse(configuration);

            stateService.disableStateActions();
            stateService.disableStateActionAssignments();
            stateService.disableStateActionEnhancements();
            stateService.disableStateTransitions();

            String feedback = importScopes(document);
            boolean outcome = feedback == null;
            return new AbstractMap.SimpleEntry<Boolean, String>(outcome, outcome ? IMPORT_SUCCESS : feedback);
        } catch (SAXException e) {
            String parseError = e.getMessage();
            return new AbstractMap.SimpleEntry<Boolean, String>(false, IMPORT_FAILURE_XML_INVALID + (parseError != null ? ". " + parseError : ""));
        } catch (IOException e) {
            return new AbstractMap.SimpleEntry<Boolean, String>(false, IMPORT_FAILURE_XML_MALFORMED);
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
    private String importScopes(Document document) {
        NodeList scopeElements = document.getElementsByTagName("scope");

        for (int i = 0; i < scopeElements.getLength(); i++) {
            Element scopeElement = (Element) scopeElements.item(i);
            String scopeElementId = scopeElement.getAttribute("id");

            PrismScope scopeId = getValueOf(PrismScope.class, scopeElementId);

            if (scopeId == null) {
                return getInvalidEntityError(PrismScope.class, scopeId);
            }

            String feedback = importStates(scopeElement, scopeId);
            if (feedback != null) {
                return feedback;
            }
        }

        return null;
    }

    // TODO: Test invalid state
    // TODO: Test invalid state scope
    private String importStates(Element scopeElement, PrismScope scopeId) {
        NodeList stateElements = scopeElement.getElementsByTagName("state");

        for (int i = 0; i < stateElements.getLength(); i++) {
            Element stateElement = (Element) stateElements.item(i);
            String stateElementId = stateElement.getAttribute("id");

            PrismState stateId = getValueOf(PrismState.class, stateElementId);

            if (stateId == null) {
                return getInvalidEntityError(PrismState.class, stateId);
            } else if (stateId.getScope() != scopeId) {
                return getInvalidEntityAssignmentError(PrismState.class, scopeId, stateId);
            }

            String feedback = importActions(stateElement, stateId);
            if (feedback != null) {
                return feedback;
            }
        }

        return null;
    }

    // TODO: Test invalid action
    // TODO: Test invalid action scope
    // TODO: Test invalid notification template
    // TODO: Test invalid notification template scope
    // TODO: Test invalid notification template type
    // TODO: Test importing new version of workflow
    private String importActions(Element stateElement, PrismState stateId) {
        NodeList actionElements = stateElement.getElementsByTagName("action");
        for (int i = 0; i < actionElements.getLength(); i++) {
            Element actionElement = (Element) actionElements.item(i);
            String actionElementId = actionElement.getAttribute("id");

            PrismAction actionId = getValueOf(PrismAction.class, actionElementId);

            if (actionId == null) {
                return getInvalidEntityError(PrismAction.class, actionId);
            } else if (stateId.getScope() != actionId.getScope()) {
                return getInvalidEntityAssignmentError(PrismAction.class, stateId, actionId);
            }

            NotificationTemplate notificationTemplate = null;
            PrismNotificationTemplate notificationTemplateId = null;
            Integer reminderInterval = null;
            String actionElementNotification = actionElement.getAttribute("notification");
            if (actionElementNotification != null) {
                notificationTemplateId = getValueOf(PrismNotificationTemplate.class, actionElementNotification);

                if (notificationTemplateId == null) {
                    return getInvalidEntityError(PrismNotificationTemplate.class, notificationTemplateId);
                } else if (actionId.getScope() != notificationTemplateId.getScope()) {
                    return getInvalidEntityAssignmentError(PrismNotificationTemplate.class, actionId, notificationTemplateId);
                } else if (notificationTemplateId.getNotificationPurpose() != PrismNotificationPurpose.REQUEST) {
                    return getInvalidNotificationTemplateError(actionElementNotification, notificationTemplateId.getNotificationPurpose());
                }

                String actionReminderInterval = actionElement.getAttribute("reminder-interval");
                if (actionReminderInterval != null) {
                    reminderInterval = Integer.parseInt(actionReminderInterval);

                    if (reminderInterval < 1) {
                        return actionReminderInterval + " " + IMPORT_INVALID_ENTITY + " " + IMPORT_INVALID_REMINDER_INTERVAL;
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

            String feedback = importRoles(actionElement, stateAction);

            if (feedback == null) {
                feedback = importStateTransitions(actionElement, stateAction);
            }

            if (feedback != null) {
                return feedback;
            }
        }

        return null;
    }

    // TODO: Test invalid action assignment
    // TODO: Test invalid action assignment scope
    private String importRoles(Element actionElement, StateAction stateAction) {
        PrismAction actionId = stateAction.getAction().getId();

        NodeList roleElements = actionElement.getElementsByTagName("role");

        for (int i = 0; i < roleElements.getLength(); i++) {
            Element roleElement = (Element) roleElements.item(i);
            String roleElementId = roleElement.getAttribute("id");

            PrismRole roleId = getValueOf(PrismRole.class, roleElementId);

            if (roleId == null) {
                return getInvalidEntityError(PrismRole.class, roleId);
            } else if (roleId.getScope().getPrecedence() < actionId.getScope().getPrecedence()) {
                return getInvalidEntityAssignmentError(PrismRole.class, actionId, roleId);
            }

            Role role = entityService.getByProperty(Role.class, "id", roleId);

            StateActionAssignment transientStateActionAssignment = new StateActionAssignment().withStateAction(stateAction).withRole(role).withEnabled(true);
            StateActionAssignment stateActionAssignment = entityService.createOrUpdate(transientStateActionAssignment);

            String feedback = importEnhancements(roleElement, stateActionAssignment);
            if (feedback != null) {
                return feedback;
            }
        }

        return null;
    }

    // TODO: Test invalid action assignment enhancement
    // TODO: Test invalid action assignment enhancement scope
    // TODO: Test invalid delegate action
    // TODO: Test invalid delegate action scope
    private String importEnhancements(Element roleElement, StateActionAssignment stateActionAssignment) {
        PrismAction actionId = stateActionAssignment.getStateAction().getAction().getId();

        NodeList enhancementElements = roleElement.getElementsByTagName("enhancement");

        for (int i = 0; i < enhancementElements.getLength(); i++) {
            Element enhancementElement = (Element) enhancementElements.item(i);
            String enhancementElementType = enhancementElement.getAttribute("type");

            PrismActionEnhancementType enhancementType = getValueOf(PrismActionEnhancementType.class, enhancementElementType);

            if (enhancementType == null) {
                return getInvalidEntityError(PrismActionEnhancementType.class, enhancementType);
            } else if (enhancementType.getScope() != actionId.getScope()) {
                return getInvalidEntityAssignmentError(PrismActionEnhancementType.class, actionId, enhancementType);
            }

            Action delegatedAction = null;
            PrismAction delegatedActionId = null;
            String enhancementElementDelegatedAction = enhancementElement.getAttribute("delegated-action");
            if (enhancementElementDelegatedAction != null) {
                delegatedActionId = getValueOf(PrismAction.class, enhancementElementDelegatedAction);

                if (delegatedActionId == null) {
                    return getInvalidEntityError(PrismAction.class, delegatedActionId);
                } else if (delegatedActionId.getScope() != actionId.getScope()) {
                    return getInvalidEntityAssignmentError(PrismAction.class, actionId, delegatedActionId);
                }

                delegatedAction = entityService.getByProperty(Action.class, "id", actionId);
            }

            StateActionEnhancement transientStateActionEnhancement = new StateActionEnhancement().withStateActionAssignment(stateActionAssignment)
                    .withEnhancementType(enhancementType).withDelegatedAction(delegatedAction).withEnabled(true);
            entityService.createOrUpdate(transientStateActionEnhancement);

        }
        return null;
    }

    // TODO: Test invalid state transition evaluation
    // TODO: Test invalid state transition evaluation assignment
    // TODO: Test invalid transition state
    // TODO: Test invalid transition state assignment
    // TODO: Test invalid transition action
    // TODO: Test invalid transition action assignment
    // TODO: Test invalid state transition (ambiguous)
    private String importStateTransitions(Element actionElement, StateAction stateAction) {
        PrismAction actionId = stateAction.getAction().getId();

        NodeList stateTransitionsElements = actionElement.getElementsByTagName("transition-states");

        if (stateTransitionsElements.getLength() > 1) {
            PrismStateTransitionEvaluation stateTransitionEvaluation = null;

            Element stateTransitionsElement = (Element) stateTransitionsElements.item(0);
            String stateTransitionsElementEvaluation = stateTransitionsElement.getAttribute("evaluation");

            if (stateTransitionsElementEvaluation != null) {
                stateTransitionEvaluation = getValueOf(PrismStateTransitionEvaluation.class, stateTransitionsElementEvaluation);

                if (stateTransitionEvaluation == null) {
                    return getInvalidEntityError(PrismStateTransitionEvaluation.class, stateTransitionEvaluation);
                } else if (!stateTransitionEvaluation.getInvokingActions().contains(actionId)) {
                    return getInvalidEntityAssignmentError(PrismStateTransitionEvaluation.class, actionId, stateTransitionEvaluation);
                }

            }

            NodeList stateTransitionElements = stateTransitionsElement.getElementsByTagName("transition-state");
            
            for (int i = 0; i < stateTransitionElements.getLength(); i++) {
                Element stateTransitionElement = (Element) stateTransitionElements.item(i);
                String stateTransitionElementStateId = stateTransitionElement.getAttribute("id");
                String stateTransitionElementActionId = stateTransitionElement.getAttribute("action");

                PrismState transitionStateId = getValueOf(PrismState.class, stateTransitionElementStateId);

                if (transitionStateId == null) {
                    return getInvalidEntityError(PrismState.class, transitionStateId);
                } else if (transitionStateId.getScope() != actionId.getScope()
                        && !(transitionStateId.getScope().getPrecedence() < actionId.getScope().getPrecedence() && actionId.name().contains("_CREATE_"))) {
                    return getInvalidEntityAssignmentError(PrismState.class, actionId, transitionStateId);
                }

                PrismAction transitionActionId = getValueOf(PrismAction.class, stateTransitionElementActionId);

                if (transitionActionId == null) {
                    return getInvalidEntityError(PrismAction.class, transitionActionId);
                } else if (transitionActionId.getScope() != actionId.getScope()) {
                    return getInvalidEntityAssignmentError(PrismAction.class, actionId, transitionActionId);
                } else if (i > 0 && stateTransitionEvaluation == null) {
                    return getInvalidStateTransitionError(stateAction.getState().getId());
                }

                // TODO: propagated actions

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

        return null;
    }

    private <T extends Enum<T>> T getValueOf(Class<T> clazz, String stringValue) {
        try {
            return Enum.valueOf(clazz, stringValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private <T extends Enum<T>> String getInvalidEntityError(Class<T> entityClass, Enum<T> value) {
        return value.name() + " " + IMPORT_INVALID_ENTITY + " " + entityClass.getSimpleName().toLowerCase();
    }

    private <T extends Enum<T>, U extends Enum<U>> String getInvalidEntityAssignmentError(Class<T> entityClass, Enum<U> parentEntity, Enum<T> value) {
        return value.name() + " " + IMPORT_INVALID_ENTITY + " " + entityClass.getSimpleName().toLowerCase() + " " + IMPORT_INVALID_ENTITY_ASSIGNMENT + " "
                + parentEntity.getClass().getSimpleName().toLowerCase() + " " + parentEntity.name().toLowerCase();
    }

    private String getInvalidNotificationTemplateError(String templateId, PrismNotificationPurpose purpose) {
        return templateId + " " + IMPORT_INVALID_ENTITY + purpose.name().toLowerCase() + " " + IMPORT_INVALID_NOTIFICATION;
    }

    private String getInvalidStateTransitionError(PrismState stateId) {
        return stateId + " " + IMPORT_INVALID_STATE_TRANSITION;
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
