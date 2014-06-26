package com.zuehlke.pgadmissions.exceptions;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.zuehlke.pgadmissions.domain.enums.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class WorkflowConfigurationException extends Exception {

    private static final long serialVersionUID = 4978904574471246299L;

    public WorkflowConfigurationException(IOException e) {
        super("Your workflow configuration is malformed");
    }

    public WorkflowConfigurationException(SAXException e) {
        super("Your workflow configuration is not compliant with our schema " + (e.getMessage() != null ? " (" + e.getMessage() + ")" : ""));
    }

    public <T extends Enum<T>> WorkflowConfigurationException(Class<T> clazz, String value) {
        super(value + " is not a valid " + clazz.getSimpleName().toLowerCase());
    }

    public <T extends Enum<T>, U extends Enum<U>> WorkflowConfigurationException(Enum<T> value, Enum<U> parentValue) {
        super(value.name() + " is not a valid " + (value.getClass() == parentValue.getClass() ? "delegated " : "")
                + value.getClass().getSimpleName().toLowerCase() + " for the " + parentValue.getClass().getSimpleName().toLowerCase() + " "
                + parentValue.name());
    }

    public WorkflowConfigurationException(PrismNotificationTemplate templateId, PrismNotificationPurpose purpose) {
        super(templateId.name() + " is not a valid " + purpose.name().toLowerCase() + " template");
    }

    public WorkflowConfigurationException(PrismState stateId) {
        super(stateId + " has no unambigious transition state");
    }

    public WorkflowConfigurationException(Integer reminderInterval) {
        super(reminderInterval.toString() + " is not a valid reminder interval");
    }

}
