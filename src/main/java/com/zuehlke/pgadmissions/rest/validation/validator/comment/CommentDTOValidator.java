package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldResolution;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.validation.validator.AbstractValidator;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CommentDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) throws Exception {
        CommentDTO comment = (CommentDTO) target;
        PrismAction action = comment.getAction();

        PrismActionValidationDefinition validationDefinition = action.getValidationDefinition();
        Map<PrismActionCommentField, List<PrismActionValidationFieldResolution>> fieldDefinitions = validationDefinition.getFieldResolutions();

        for (PrismActionCommentField field : PrismActionCommentField.values()) {
            List<PrismActionValidationFieldResolution> resolutions = fieldDefinitions.get(field);
            String fieldName = field.getFieldName();
            Object fieldValue = PropertyUtils.getSimpleProperty(comment, fieldName);
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
                            Collection collection = (Collection) fieldValue;
                            Integer min = (Integer) fieldResolution.getArguments().get("min");
                            Integer max = (Integer) fieldResolution.getArguments().get("max");
                            if (min != null && min > 0 && (collection == null || collection.size() < min)) {
                                errors.rejectValue(fieldName, "min", new Object[]{min}, null);
                            } else if (max != null && collection != null && collection.size() > max) {
                                errors.rejectValue(fieldName, "max", new Object[]{max}, null);
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
