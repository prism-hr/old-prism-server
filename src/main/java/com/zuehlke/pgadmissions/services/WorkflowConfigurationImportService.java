package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
@Transactional(timeout = 60)
public class WorkflowConfigurationImportService {

    private final String WORKFLOW_CONFIGURATION_XSD = "xml/workflow_configuration_schema.xsd";

    private static final String IMPORT_SUCCESS = "Your workflow configuration has been applied successfully";

    private static final String IMPORT_FAILURE_XML_INVALID = "Your workflow configuration is not compliant with our schema";

    private static final String IMPORT_FAILURE_XML_MALFORMED = "Your workflow configuration is malformed";

    private static final String IMPORT_INVALID_ENTITY = "is not a not a valid";

    private static final String IMPORT_INVALID_ENTITY_ASSIGNMENT = "for the";

    private static final String IMPORT_INVALID_NOTIFICATION = "template";

    private static final String IMPORT_INVALID_REMINDER_INTERVAL = "reminder interval";

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

            String feedback = importScopes(document);
            boolean outcome = feedback.length() == 0;
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
    private String importScopes(Document document) {
        String feedback = "";
        NodeList scopeElements = document.getElementsByTagName("scope");
        for (int i = 0; i < scopeElements.getLength(); i++) {
            Element scopeElement = (Element) scopeElements.item(i);
            String scopeElementId = scopeElement.getAttribute("id");

            PrismScope scopeId = getValueOf(PrismScope.class, scopeElementId);

            if (scopeId == null) {
                feedback = getInvalidEntityError(PrismScope.class, scopeElementId);
            }

            feedback = importStates(scopeElement, scopeId);
        }

        return feedback;
    }

    // TODO: Test invalid state
    // TODO: Test invalid state scope
    private String importStates(Element scopeElement, PrismScope scopeId) {
        String feedback = "";
        NodeList stateElements = scopeElement.getElementsByTagName("state");

        for (int j = 0; j < stateElements.getLength(); j++) {
            Element stateElement = (Element) stateElements.item(j);
            String stateElementId = stateElement.getAttribute("id");

            PrismState stateId = getValueOf(PrismState.class, stateElementId);

            if (stateId == null) {
                feedback = getInvalidEntityError(PrismState.class, stateElementId);
            } else if (stateId.getScope() != scopeId) {
                feedback = getInvalidEntityAssignmentError(PrismState.class, scopeId, stateElementId);
            }

            feedback = importActions(stateElement, stateId);
        }

        return feedback;
    }

    // TODO: Test invalid action
    // TODO: Test invalid action scope
    // TODO: Test invalid notification template
    // TODO: Test invalid notification template scope
    // TODO: Test invalid notification template type
    // TODO: Test importing new version of workflow
    private String importActions(Element stateElement, PrismState stateId) {
        String feedback = "";
        NodeList actionElements = stateElement.getElementsByTagName("action");
        for (int i = 0; i < actionElements.getLength(); i++) {
            Element actionElement = (Element) actionElements.item(i);
            String actionElementId = actionElement.getAttribute("id");

            PrismAction actionId = getValueOf(PrismAction.class, actionElementId);

            if (actionId == null) {
                feedback = getInvalidEntityError(PrismAction.class, actionElementId);
            } else if (stateId.getScope() != actionId.getScope()) {
                feedback = getInvalidEntityAssignmentError(PrismAction.class, stateId, actionElementId);
            }

            NotificationTemplate notificationTemplate = null;
            PrismNotificationTemplate notificationTemplateId = null;
            Integer reminderInterval = null;
            String actionElementNotification = actionElement.getAttribute("notification");
            if (actionElementNotification != null) {
                notificationTemplateId = getValueOf(PrismNotificationTemplate.class, actionElementNotification);

                if (notificationTemplateId == null) {
                    feedback = getInvalidEntityError(PrismNotificationTemplate.class, actionElementNotification);
                } else if (actionId.getScope() != notificationTemplateId.getScope()) {
                    feedback = getInvalidEntityAssignmentError(PrismNotificationTemplate.class, actionId.getScope(), actionElementNotification);
                } else if (notificationTemplateId.getNotificationPurpose() != PrismNotificationPurpose.REQUEST) {
                    feedback = getInvalidNotificationTemplateError(actionElementNotification, notificationTemplateId.getNotificationPurpose());
                }

                String actionReminderInterval = actionElement.getAttribute("reminder-interval");
                if (actionReminderInterval != null) {
                    reminderInterval = Integer.parseInt(actionReminderInterval);

                    if (reminderInterval < 1) {
                        feedback = actionReminderInterval + " " + IMPORT_INVALID_ENTITY + " " + IMPORT_INVALID_REMINDER_INTERVAL;
                    }
                }

                notificationTemplate = entityService.getByProperty(NotificationTemplate.class, "id", notificationTemplateId);
            }

            State state = entityService.getByProperty(State.class, "id", stateId);
            Action action = entityService.getByProperty(Action.class, "id", actionId);
            boolean raisesUrgentFlag = actionElement.getAttribute("urgent") == "true";
            boolean defaultAction = actionElement.getAttribute("default") == "true";

            StateAction transientStateAction = new StateAction().withState(state).withAction(action).withRaisesUrgentFlag(raisesUrgentFlag)
                    .withDefaultAction(defaultAction).withNotificationTemplate(notificationTemplate);
            entityService.createOrUpdate(transientStateAction);
            
            if (reminderInterval != null) {
                NotificationConfiguration configuration = notificationService.getConfiguration(systemService.getSystem(), notificationTemplate);
                configuration.setReminderInterval(reminderInterval);
            }
        }

        return feedback;
    }

    private <T extends Enum<T>> T getValueOf(Class<T> clazz, String stringValue) {
        try {
            return Enum.valueOf(clazz, stringValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private <T extends Enum<T>> String getInvalidEntityError(Class<T> entityClass, String stringValue) {
        return WordUtils.capitalizeFully(stringValue) + " " + IMPORT_INVALID_ENTITY + " " + entityClass.getSimpleName().toLowerCase();
    }

    private <T extends Enum<T>, U extends Enum<U>> String getInvalidEntityAssignmentError(Class<T> entityClass, Enum<U> parentEntity, String stringValue) {
        return WordUtils.capitalizeFully(stringValue) + " " + IMPORT_INVALID_ENTITY + " " + entityClass.getSimpleName().toLowerCase() + " "
                + IMPORT_INVALID_ENTITY_ASSIGNMENT + " " + parentEntity.getClass().getSimpleName().toLowerCase() + " " + parentEntity.name().toLowerCase();
    }

    private String getInvalidNotificationTemplateError(String templateId, PrismNotificationPurpose purpose) {
        return WordUtils.capitalizeFully(templateId) + " " + IMPORT_INVALID_ENTITY + purpose.name().toLowerCase() + " " + IMPORT_INVALID_NOTIFICATION;
    }

}
