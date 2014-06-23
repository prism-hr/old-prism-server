package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

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

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
@Transactional
public class WorkflowConfigurationImportService {
    
    private final String WORKFLOW_CONFIGURATION_XSD = "xml/workflow_configuration_schema.xsd";
    
    private static final String IMPORT_SUCCESS = "Your workflow configuration has been applied successfully";
    
    private static final String IMPORT_FAILURE_XML_INVALID = "Your workflow configuration is not compliant with our schema";
    
    private static final String IMPORT_FAILURE_XML_MALFORMED = "Your workflow configuration is malformed";
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private SystemService systemService;

    public SimpleEntry<Boolean, String> importWorkflowConfiguration(String configuration) {
        try {
            DocumentBuilder documentBuilder = prepareDocumentBuilder();
            Document document = documentBuilder.parse(configuration);
            NodeList scopeElements = document.getElementsByTagName("scope");
            for (int i = 0; i < scopeElements.getLength(); i++) {
                Element scopeElement = (Element) scopeElements.item(i);
                // TODO: (Test) invalid scope
                String importError = importScopeDefinition(scopeElement);
                if (importError.length() > 0) {
                    return new AbstractMap.SimpleEntry<Boolean, String>(false, importError);
                }
            }
            return new AbstractMap.SimpleEntry<Boolean, String>(true, IMPORT_SUCCESS);
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
    
    private String importScopeDefinition(Element scopeElement) {
        String importError = PrismScope.contains(scopeElement.getAttribute("id"));
        NodeList stateElements = scopeElement.getElementsByTagName("state");
        for (int i = 0; i < stateElements.getLength(); i++) {
            Element stateElement = (Element) stateElements.item(i);
            importStateDefinition(null, stateElement);
        }
        return importError;
    }

    private void importStateDefinition(Scope scope, Element stateElement) {
        State parentState = (State) entityService.getOrCreate(new State(PrismState.valueOf(stateElement.getAttribute("parent-state")), scope));
        parentState.setParentState(parentState);

        State state = (State) entityService.getOrCreate(new State(PrismState.valueOf(stateElement.getAttribute("id")), scope));
        state.setParentState(parentState);

        NodeList actionElements = stateElement.getElementsByTagName("action");
        HashMap<Element, Action> actionInserts = Maps.newHashMap();
        for (int i = 0; i < actionElements.getLength(); i++) {
            Element actionElement = (Element) actionElements.item(i);
            importActionDefinition(actionElement, scope, actionInserts);
        }

    }

    private void importActionDefinition(Element actionElement, Scope scope, HashMap<Element, Action> actionInserts) {
        Action action = (Action) entityService.getByProperty(Action.class, "id", PrismAction.valueOf(actionElement.getAttribute("id")));
        if (actionElement.hasAttribute("delegate-action")) {
            actionInserts.put(actionElement, action);
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

}
