package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.DISPLAY_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.representation.configuration.DisplayPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {

	private Resource resource;

	private PrismLocale locale;

	private PrismProgramType programType;

	private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

	@Inject
	private CustomizationService customizationService;

	public String load(PrismDisplayPropertyDefinition property) throws Exception {
		String value = properties.get(property);
		if (value == null) {
			PrismDisplayPropertyCategory category = property.getCategory();
			properties.putAll(getDisplayProperties(resource, property.getCategory().getScope(), category, locale, programType));
			value = properties.get(property);
		}
		return value;
	}

	public String load(PrismDisplayPropertyDefinition trueIndex, PrismDisplayPropertyDefinition falseIndex, boolean evaluation) throws Exception {
		return evaluation ? load(trueIndex) : load(falseIndex);
	}

	public PropertyLoader localize(Resource resource) {
		return localize(resource, (PrismLocale) null);
	}

	public PropertyLoader localize(Resource resource, User user) {
		return localize(resource, user.getLocale());
	}

	public PropertyLoader localize(Resource resource, PrismLocale prismLocale) {
		PrismScope resourceScope = resource.getResourceScope();
		if (resourceScope.ordinal() > INSTITUTION.ordinal()) {
			Program program = resource.getProgram();
			this.resource = program;
			this.programType = program.getImportedProgramType().getPrismProgramType();
		} else {
			this.resource = resource;
			this.programType = null;
		}
		this.locale = prismLocale;
		return this;
	}

	private HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope scope, PrismDisplayPropertyCategory category,
	        PrismLocale locale, PrismProgramType programType) throws Exception {
		List<WorkflowConfigurationRepresentation> values = customizationService.getConfigurationRepresentations(DISPLAY_PROPERTY, resource, scope, locale,
		        programType, category);
		HashMap<PrismDisplayPropertyDefinition, String> displayProperties = Maps.newHashMap();
		for (WorkflowConfigurationRepresentation value : values) {
			DisplayPropertyConfigurationRepresentation displayValue = (DisplayPropertyConfigurationRepresentation) value;
			PrismDisplayPropertyDefinition displayPropertyId = (PrismDisplayPropertyDefinition) value.getDefinitionId();
			if (!displayProperties.containsKey(displayPropertyId)) {
				displayProperties.put(displayPropertyId, displayValue.getValue());
			}
		}
		return displayProperties;
	}

}
