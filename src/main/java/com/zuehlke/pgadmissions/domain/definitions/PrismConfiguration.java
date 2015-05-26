package com.zuehlke.pgadmissions.domain.definitions;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.Map;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateDurationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.rest.representation.configuration.ActionCustomQuestionConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.NotificationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.StateDurationConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.NotificationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateDurationDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowPropertyDefinitionRepresentation;

public enum PrismConfiguration {

	CUSTOM_QUESTION(ActionCustomQuestionConfiguration.class, ActionCustomQuestionDefinition.class, ActionCustomQuestionConfigurationRepresentation.class,
	        WorkflowDefinitionRepresentation.class, true, true, null, null, "_COMMENT_UPDATED_ACTION_PROPERTY", false, new String[] { "id" }), //
	DISPLAY_PROPERTY(DisplayPropertyConfiguration.class, DisplayPropertyDefinition.class, DisplayPropertyConfigurationRepresentation.class,
	        WorkflowDefinitionRepresentation.class, true, false, null, null, "_COMMENT_UPDATED_DISPLAY_PROPERTY", true, new String[] { "category", "id" }), //
	NOTIFICATION(NotificationConfiguration.class, NotificationDefinition.class, NotificationConfigurationRepresentation.class,
	        NotificationDefinitionRepresentation.class, false, false, 1, 28, "_COMMENT_UPDATED_NOTIFICATION", true, new String[] { "id" }), //
	STATE_DURATION(StateDurationConfiguration.class, StateDurationDefinition.class, StateDurationConfigurationRepresentation.class,
	        StateDurationDefinitionRepresentation.class, true, false, 1, 364, "_COMMENT_UPDATED_STATE_DURATION", true, new String[] { "id" }), //
	WORKFLOW_PROPERTY(WorkflowPropertyConfiguration.class, WorkflowPropertyDefinition.class, WorkflowPropertyConfigurationRepresentation.class,
	        WorkflowPropertyDefinitionRepresentation.class, true, true, null, null, "_COMMENT_UPDATED_WORKFLOW_PROPERTY", true, new String[] {
	                "category", "id" });

	private Class<? extends WorkflowConfiguration> configurationClass;

	private Class<? extends WorkflowDefinition> definitionClass;

	private Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass;

	private Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass;

	private boolean grouped;

	private boolean versioned;

	private Integer minimumPermitted;

	private Integer maximumPermitted;

	private String updateCommentProperty;

	private boolean validateResponseSize;

	private String[] orderColumns;

	private static Map<Class<? extends WorkflowConfiguration>, PrismConfiguration> reverseMap = Maps.newHashMap();

	static {
		for (PrismConfiguration type : values()) {
			reverseMap.put(type.getConfigurationClass(), type);
		}
	}

	PrismConfiguration(Class<? extends WorkflowConfiguration> configurationClass, Class<? extends WorkflowDefinition> definitionClass,
	        Class<? extends WorkflowConfigurationRepresentation> configurationRepresentationClass,
	        Class<? extends WorkflowDefinitionRepresentation> definitionRepresentationClass, boolean grouped, boolean versioned, Integer minimumPermitted,
	        Integer maximumPermitted, String updateCommentProperty, boolean validateResponseSize, String[] orderColumns) {
		this.configurationClass = configurationClass;
		this.definitionClass = definitionClass;
		this.configurationRepresentationClass = configurationRepresentationClass;
		this.definitionRepresentationClass = definitionRepresentationClass;
		this.grouped = grouped;
		this.versioned = versioned;
		this.minimumPermitted = minimumPermitted;
		this.maximumPermitted = maximumPermitted;
		this.updateCommentProperty = updateCommentProperty;
		this.validateResponseSize = validateResponseSize;
		this.orderColumns = orderColumns;
	}

	public Class<? extends WorkflowConfiguration> getConfigurationClass() {
		return configurationClass;
	}

	public Class<? extends WorkflowDefinition> getDefinitionClass() {
		return definitionClass;
	}

	public Class<? extends WorkflowConfigurationRepresentation> getConfigurationRepresentationClass() {
		return configurationRepresentationClass;
	}

	public Class<? extends WorkflowDefinitionRepresentation> getDefinitionRepresentationClass() {
		return definitionRepresentationClass;
	}

	public boolean isGrouped() {
		return grouped;
	}

	public boolean isVersioned() {
		return versioned;
	}

	public Integer getMinimumPermitted() {
		return minimumPermitted;
	}

	public Integer getMaximumPermitted() {
		return maximumPermitted;
	}

	public String getUpdateCommentProperty() {
		return updateCommentProperty;
	}

	public String getDefinitionPropertyName() {
		return UPPER_CAMEL.to(LOWER_CAMEL, definitionClass.getSimpleName());
	}

	public boolean isValidateResponseSize() {
		return validateResponseSize;
	}

	public boolean isCategorizable() {
		try {
			return PrismConfigurationCategorizable.class.isAssignableFrom(definitionClass.getDeclaredField("id").getType());
		} catch (NoSuchFieldException e) {
			throw new Error(e);
		}
	}

	public String[] getOrderColumns() {
		return orderColumns;
	}

}
