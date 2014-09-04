package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.Maps;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrismActionValidationDefinition {

    private Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions;

    private Validator customValidator;

    public PrismActionValidationDefinition(Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions) {
        this(fieldResolutions, null);
    }

    public PrismActionValidationDefinition(Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldResolutions, Validator customValidator) {
        this.fieldResolutions = fieldResolutions;
        this.customValidator = customValidator;
    }

    public Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> getFieldResolutions() {
        return fieldResolutions;
    }

    public Validator getCustomValidator() {
        return customValidator;
    }

    public static PrismActionValidationDefinitionBuilder builder() {
        return new PrismActionValidationDefinitionBuilder();
    }

    public static class PrismActionValidationDefinitionBuilder {

        private Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldDefinitions = Maps.newLinkedHashMap();

        private Validator customValidator;

        public PrismActionValidationDefinitionBuilder addResolution(PrismActionCommentField field, PrismActionValidationFieldResolution... resolutions) {
            fieldDefinitions.put(field, Arrays.asList(resolutions));
            return this;
        }

        public PrismActionValidationDefinitionBuilder addResolution(PrismActionCommentField field, PrismActionValidationFieldRestriction restriction) {
            fieldDefinitions.put(field, Collections.singletonList(new PrismActionValidationFieldResolution(restriction)));
            return this;
        }

        public PrismActionValidationDefinitionBuilder setCustomValidator(Validator customValidator) {
            this.customValidator = customValidator;
            return this;
        }

        public PrismActionValidationDefinition build() {
            return new PrismActionValidationDefinition(fieldDefinitions, customValidator);
        }
    }
}
