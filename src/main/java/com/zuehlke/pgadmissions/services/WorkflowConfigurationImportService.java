package com.zuehlke.pgadmissions.services;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private SystemService systemService;

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
