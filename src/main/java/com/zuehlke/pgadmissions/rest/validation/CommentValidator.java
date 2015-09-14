package com.zuehlke.pgadmissions.rest.validation;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_APPOINTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_OFFER_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL;
import static java.util.Arrays.asList;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.services.CustomizationService;

@Component
public class CommentValidator extends LocalValidatorFactoryBean implements Validator {

    @Inject
    private CustomizationService customizationService;

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
        validateConfiguredProperties(comment, action, errors);
    }

    private void validateConfiguredProperties(Comment comment, PrismAction action, Errors errors) {
        if (asList(APPLICATION_ASSIGN_HIRING_MANAGERS, APPLICATION_CONFIRM_APPOINTMENT, APPLICATION_CONFIRM_OFFER_RECOMMENDATION).contains(action)) {
            PrismConfiguration configurationType = WORKFLOW_PROPERTY;
            Integer workflowPropertyConfigurationVersion = comment.getResource().getWorkflowPropertyConfigurationVersion();

            WorkflowPropertyConfiguration positionDetailConfiguration = (WorkflowPropertyConfiguration) //
            customizationService.getConfigurationWithVersion(configurationType, APPLICATION_POSITION_DETAIL, workflowPropertyConfigurationVersion);

            if (positionDetailConfiguration != null && BooleanUtils.isTrue(positionDetailConfiguration.getRequired())) {
                ValidationUtils.rejectIfEmpty(errors, "positionDetail", "notNull");
            }

            WorkflowPropertyConfiguration offerDetailConfiguration = (WorkflowPropertyConfiguration) //
            customizationService.getConfigurationWithVersion(configurationType, APPLICATION_OFFER_DETAIL, workflowPropertyConfigurationVersion);

            if (offerDetailConfiguration != null && BooleanUtils.isTrue(offerDetailConfiguration.getRequired())) {
                ValidationUtils.rejectIfEmpty(errors, "offerDetail", "notNull");
            }
        }
    }

}
