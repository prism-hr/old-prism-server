package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.Validator;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PrismActionValidationDefinition {

	private Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions;

	private Set<PrismActionValidationFieldResolutionCaveat> fieldCaveats;

	private Validator customValidator;

	public PrismActionValidationDefinition(Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions) {
		this(fieldResolutions, null);
	}

	public PrismActionValidationDefinition(Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions,
	        Set<PrismActionValidationFieldResolutionCaveat> fieldCaveats) {
		this(fieldResolutions, fieldCaveats, null);
	}

	public PrismActionValidationDefinition(Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions,
	        Set<PrismActionValidationFieldResolutionCaveat> fieldCaveats, Validator customValidator) {
		this.fieldResolutions = fieldResolutions;
		this.fieldCaveats = fieldCaveats;
		this.customValidator = customValidator;
	}

	public Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> getFieldResolutions() {
		return fieldResolutions;
	}
	
	public Set<PrismActionValidationFieldResolutionCaveat> getFieldCaveats() {
		return fieldCaveats;
	}

	public Validator getCustomValidator() {
		return customValidator;
	}

	public static PrismActionValidationDefinitionBuilder builder() {
		return new PrismActionValidationDefinitionBuilder();
	}

	public static class PrismActionValidationDefinitionBuilder {

		private Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions = Maps.newLinkedHashMap();

		private Set<PrismActionValidationFieldResolutionCaveat> fieldCaveats = Sets.newLinkedHashSet();

		private Validator customValidator;

		public PrismActionValidationDefinitionBuilder addResolution(PrismActionCommentField field, PrismActionValidationFieldResolution... resolutions) {
			fieldResolutions.put(field, Arrays.asList(resolutions));
			return this;
		}

		public PrismActionValidationDefinitionBuilder addResolution(PrismActionCommentField field, PrismActionValidationFieldRestriction restriction) {
			fieldResolutions.put(field, Collections.singletonList(new PrismActionValidationFieldResolution(restriction)));
			return this;
		}

		public PrismActionValidationDefinitionBuilder addCaveat(PrismActionValidationFieldResolutionCaveat fieldCaveat) {
			this.fieldCaveats.add(fieldCaveat);
			return this;
		}

		public PrismActionValidationDefinitionBuilder setCustomValidator(Validator customValidator) {
			this.customValidator = customValidator;
			return this;
		}

		public PrismActionValidationDefinition build() {
			return new PrismActionValidationDefinition(fieldResolutions, fieldCaveats, customValidator);
		}
	}

}
