package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPLICATION_RATING;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldResolution;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.rest.dto.CommentCustomResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
@SuppressWarnings("unchecked")
public class CommentValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private EntityService entityService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        CommentDTO comment = (CommentDTO) target;
        PrismAction action = comment.getAction();

        PrismActionValidationDefinition validationDefinition = action.getValidationDefinition();
        if (validationDefinition == null) {
            return;
        }
        Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldDefinitions = validationDefinition.getFieldResolutions();

        List<CommentCustomResponseDTO> customResponseDTOs = comment.getCustomResponses();
        boolean validateRating = true;

        if (customResponseDTOs != null) {

            int i = 0;
            Integer version = null;
            for (CommentCustomResponseDTO customResponseDTO : customResponseDTOs) {
                errors.pushNestedPath("customResponses[" + i + "]");
                ActionCustomQuestionConfiguration configuration = entityService.getById(ActionCustomQuestionConfiguration.class, customResponseDTO.getId());
                version = version == null ? configuration.getVersion() : version;

                Object propertyValue = customResponseDTO.getPropertyValue();
                PrismCustomQuestionType propertyType = configuration.getCustomQuestionType();

                if (configuration.getRequired()) {
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propertyValue", "notEmpty");
                }

                if (propertyValue != null) {
                    if (propertyType.getPermittedValues() != null) {
                        Preconditions.checkArgument(propertyType.getPermittedValues().contains(propertyValue));
                    }
                }

                if (propertyType.name().startsWith("RATING")) {
                    validateRating = false;
                }
                i++;
                errors.popNestedPath();
            }

            List<ActionCustomQuestionConfiguration> configurations = (List<ActionCustomQuestionConfiguration>) (List<?>) customizationService
                    .getConfigurationsWithVersion(PrismConfiguration.CUSTOM_QUESTION, version);

            Preconditions.checkArgument(customResponseDTOs.size() == configurations.size());
        }

        for (PrismActionCommentField field : PrismActionCommentField.values()) {
            if (field != APPLICATION_RATING || (field == APPLICATION_RATING && validateRating)) {
                List<PrismActionValidationFieldResolution> resolutions = fieldDefinitions.get(field);
                String fieldName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.name());
                Object fieldValue = ReflectionUtils.getProperty(comment, fieldName);
                if (resolutions != null) {
                    for (PrismActionValidationFieldResolution fieldResolution : resolutions) {
                        switch (fieldResolution.getRestriction()) {
                        case NOT_NULL:
                            ValidationUtils.rejectIfEmpty(errors, fieldName, "notNull");
                            break;
                        case NOT_EMPTY:
                            ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "notEmpty");
                            break;
                        case SIZE:
                            Collection<?> collection = (Collection<?>) fieldValue;
                            Integer min = (Integer) fieldResolution.getArguments().get("min");
                            Integer max = (Integer) fieldResolution.getArguments().get("max");
                            if (min != null && min > 0 && (collection == null || collection.size() < min)) {
                                errors.rejectValue(fieldName, "min", new Object[] { min }, null);
                            } else if (max != null && collection != null && collection.size() > max) {
                                errors.rejectValue(fieldName, "max", new Object[] { max }, null);
                            }
                        }
                    }
                } else {
                    if (fieldValue != null) {
                        errors.rejectValue(fieldName, "forbidden");
                    }
                }
            }

            Validator customValidator = validationDefinition.getCustomValidator();
            if (customValidator != null) {
                ValidationUtils.invokeValidator(customValidator, comment, errors);
            }
        }
    }

}
