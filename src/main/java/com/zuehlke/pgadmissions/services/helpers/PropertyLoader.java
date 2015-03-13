package com.zuehlke.pgadmissions.services.helpers;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.DisplayPropertyService;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {

	private Resource resource;

	private PrismLocale locale;

	private PrismProgramType programType;

	private final HashMap<PrismDisplayPropertyDefinition, String> properties = Maps.newHashMap();

	@Autowired
	private DisplayPropertyService displayPropertyService;

	@Autowired
	private ResourceService resourceService;

	public String load(PrismDisplayPropertyDefinition property) {
		String value = properties.get(property);
		if (value == null) {
			PrismDisplayPropertyCategory category = property.getDisplayCategory();
			properties.putAll(displayPropertyService.getDisplayProperties(resource, property.getDisplayCategory().getScope(), category, locale, programType));
			value = properties.get(property);
		}
		return value;
	}

	public String load(PrismDisplayPropertyDefinition trueIndex, PrismDisplayPropertyDefinition falseIndex, boolean evaluation) {
		return evaluation ? load(trueIndex) : load(falseIndex);
	}

	public PropertyLoader localize(Resource resource) {
		return localize(resource, null);
	}

	public PropertyLoader localize(Resource resource, PrismLocale prismLocale) {
		PrismScope resourceScope = resource.getResourceScope();
		if (Arrays.asList(PROGRAM, PROJECT, APPLICATION).contains(resourceScope)) {
			Program program = resource.getProgram();
			this.resource = program;
			this.programType = program.getProgramType().getPrismProgramType();
		} else {
			this.resource = resource;
			this.programType = null;
		}
		this.locale = resourceService.getOperativeLocale(resource, prismLocale);
		return this;
	}

}
