package com.zuehlke.pgadmissions.rest.validation.validator;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_OFFER_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
@SuppressWarnings("unchecked")
public class CommentValidator extends LocalValidatorFactoryBean implements Validator {

	@Inject
	private CustomizationService customizationService;

	@Inject
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
		validateCustomResponses(comment, errors);
		validateConfiguredProperties(comment, action, errors);
	}

	private void validateCustomResponses(Comment comment, Errors errors) {
		Set<CommentCustomResponse> customResponses = comment.getCustomResponses();
		if (customResponses != null) {
			int i = 0;
			Integer version = null;
			for (CommentCustomResponse customResponse : customResponses) {
				errors.pushNestedPath("customResponses[" + i + "]");
				ActionCustomQuestionConfiguration configuration = entityService.getById(ActionCustomQuestionConfiguration.class, //
				        customResponse.getActionCustomQuestionConfiguration().getId());
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
				
				i++;
				errors.popNestedPath();
			}

			List<ActionCustomQuestionConfiguration> configurations = (List<ActionCustomQuestionConfiguration>) (List<?>) customizationService
			        .getConfigurationsWithVersion(PrismConfiguration.CUSTOM_QUESTION, version);

			Preconditions.checkArgument(customResponses.size() <= configurations.size());
		}
	}

	private void validateConfiguredProperties(Comment comment, PrismAction action, Errors errors) {
		if (Arrays.asList(APPLICATION_ASSIGN_SUPERVISORS, APPLICATION_CONFIRM_PRIMARY_SUPERVISION,
		        APPLICATION_CONFIRM_OFFER_RECOMMENDATION).contains(action)) {
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
