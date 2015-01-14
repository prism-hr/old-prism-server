package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPLICATION_RATING;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldResolution;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
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
        return Comment.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, errors, new Object[0]);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        Comment comment = (Comment) target;
        PrismAction action = comment.getAction().getId();

        boolean validateRating = validateCustomResponses(comment, errors);

        PrismActionValidationDefinition validationDefinition = action.getValidationDefinition();
        if (validationDefinition == null) {
            return;
        }
        Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldDefinitions = validationDefinition.getFieldResolutions();

        for (PrismActionCommentField field : PrismActionCommentField.values()) {
            if (field != APPLICATION_RATING || (field == APPLICATION_RATING && validateRating)) {
                List<PrismActionValidationFieldResolution> resolutions = fieldDefinitions.get(field);
                String propertyPath = field.getPropertyPath();
                Object fieldValue = ReflectionUtils.getNestedProperty(comment, propertyPath, true);
                if (resolutions != null) {
                    for (PrismActionValidationFieldResolution fieldResolution : resolutions) {
                        switch (fieldResolution.getRestriction()) {
                        case NOT_NULL:
                            ValidationUtils.rejectIfEmpty(errors, propertyPath, "notNull");
                            break;
                        case NOT_EMPTY:
                            ValidationUtils.rejectIfEmptyOrWhitespace(errors, propertyPath, "notEmpty");
                            break;
                        case SIZE:
                            Collection<?> collection = (Collection<?>) fieldValue;
                            Integer min = (Integer) fieldResolution.getArguments().get("min");
                            Integer max = (Integer) fieldResolution.getArguments().get("max");
                            if (min != null && min > 0 && (collection == null || collection.size() < min)) {
                                errors.rejectValue(propertyPath, "min", new Object[] { min }, null);
                            } else if (max != null && collection != null && collection.size() > max) {
                                errors.rejectValue(propertyPath, "max", new Object[] { max }, null);
                            }
                        }
                    }
                } else {
                    if (fieldValue instanceof Collection) {
                        Collection<?> fieldCollection = (Collection<?>) fieldValue;
                        if (!fieldCollection.isEmpty()) {
                            errors.rejectValue(propertyPath, "forbidden");
                        }
                    } else if (fieldValue != null) {
                        errors.rejectValue(propertyPath, "forbidden");
                    }
                }
            }

            Validator customValidator = validationDefinition.getCustomValidator();
            if (customValidator != null) {
                ValidationUtils.invokeValidator(customValidator, comment, errors);
            }
        }

        validateConfiguredProperties(comment, action, errors);
    }

    private boolean validateCustomResponses(Comment comment, Errors errors) {
        boolean validateRating = true;
        Set<CommentCustomResponse> customResponses = comment.getCustomResponses();

        if (customResponses != null) {

            int i = 0;
            Integer version = null;
            for (CommentCustomResponse customResponse : customResponses) {
                errors.pushNestedPath("customResponses[" + i + "]");
                ActionCustomQuestionConfiguration configuration = entityService.getById(ActionCustomQuestionConfiguration.class, customResponse.getId());
                version = version == null ? configuration.getVersion() : version;

                Object propertyValue = customResponse.getPropertyValue();
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

            Preconditions.checkArgument(customResponses.size() == configurations.size());
        }
        return validateRating;
    }

    private void validateConfiguredProperties(Comment comment, PrismAction action, Errors errors) {
        if (Arrays.asList(PrismAction.APPLICATION_ASSIGN_SUPERVISORS, PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION,
                PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION).contains(action)) {
            PrismConfiguration configurationType = PrismConfiguration.WORKFLOW_PROPERTY;
            Integer workflowPropertyConfigurationVersion = comment.getResource().getWorkflowPropertyConfigurationVersion();

            WorkflowPropertyConfiguration positionDetailConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    configurationType, PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL, workflowPropertyConfigurationVersion);

            if (positionDetailConfiguration != null && BooleanUtils.isTrue(positionDetailConfiguration.getRequired())) {
                ValidationUtils.rejectIfEmpty(errors, "positionDetail", "notNull");
            }

            WorkflowPropertyConfiguration offerDetailConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    configurationType, PrismWorkflowPropertyDefinition.APPLICATION_OFFER_DETAIL, workflowPropertyConfigurationVersion);

            if (offerDetailConfiguration != null && BooleanUtils.isTrue(offerDetailConfiguration.getRequired())) {
                ValidationUtils.rejectIfEmpty(errors, "offerDetail", "notNull");
            }
        }
    }

}
