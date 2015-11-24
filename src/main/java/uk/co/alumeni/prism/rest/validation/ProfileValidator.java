package uk.co.alumeni.prism.rest.validation;

import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationProgramDetail;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileDocument;
import uk.co.alumeni.prism.domain.profile.ProfileEntity;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.exceptions.PrismCannotApplyException;
import uk.co.alumeni.prism.mapping.ApplicationMapper;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationStartDateRepresentation;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.utils.PrismReflectionUtils;

@Component
public class ProfileValidator extends LocalValidatorFactoryBean implements Validator {

    @Inject
    private ResourceService resourceService;

    @Inject
    private ApplicationMapper applicationMapper;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, errors, new Object[0]);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        ProfileEntity<?, ?, ?, ?, ?, ?, ?> profile = (ProfileEntity<?, ?, ?, ?, ?, ?, ?>) target;

        if (profile.getClass().equals(Application.class)) {
            ValidationUtils.rejectIfEmpty(errors, "programDetail", "notNull");
            validateStartDate((Application) profile, ((Application) profile).getProgramDetail(), errors);
        }

        ValidationUtils.rejectIfEmpty(errors, "personalDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "address", "notNull");

        for (PrismWorkflowConstraint constraint : PrismWorkflowConstraint.values()) {
            switch (constraint) {
            case APPLICATION_REFEREE_ASSIGNMENT:
                validateRangeConstraint(profile, "referees", constraint, errors);
                break;
            case APPLICATION_DOCUMENT_COVERING_LETTER:
                validateDocumentConstraint(profile, "coveringLetter", constraint, errors);
                break;
            case APPLICATION_DOCUMENT_CV:
                validateDocumentConstraint(profile, "cv", constraint, errors);
                break;
            case APPLICATION_EMPLOYMENT_POSITION:
                validateRangeConstraint(profile, "employmentPositions", constraint, errors);
                break;
            case APPLICATION_QUALIFICATION:
                validateRangeConstraint(profile, "qualifications", constraint, errors);
                break;
            default:
                break;
            }
        }

    }

    private void validateStartDate(Application application, ApplicationProgramDetail programDetail, Errors errors) {
        if (programDetail != null) {
            errors.pushNestedPath("programDetail");
            LocalDate startDate = programDetail.getStartDate();

            ResourceParent parentResource = application.getParentResource();
            if (ResourceOpportunity.class.isAssignableFrom(parentResource.getClass())) {
                List<PrismStudyOption> otherStudyOptions = resourceService.getStudyOptions((ResourceOpportunity) parentResource);
                if (otherStudyOptions.isEmpty()) {
                    throw new PrismCannotApplyException();
                }

                if (resourceService.getResourceStudyOption((ResourceOpportunity) parentResource, programDetail.getStudyOption()) == null) {
                    errors.rejectValue("studyOption", "notAvailable");
                }
            }

            ApplicationStartDateRepresentation startDateConstraints = applicationMapper.getApplicationStartDateRepresentation(new LocalDate());
            LocalDate earliestStartDate = startDateConstraints.getEarliestDate();
            LocalDate latestStartDate = startDateConstraints.getLatestDate();
            if (startDate.isBefore(earliestStartDate)) {
                errors.rejectValue("startDate", "notBefore", new Object[] { earliestStartDate }, null);
            } else if (startDate.isAfter(latestStartDate)) {
                errors.rejectValue("startDate", "notAfter", new Object[] { latestStartDate }, null);
            }

            errors.popNestedPath();
        }
    }

    private void validateRangeConstraint(ProfileEntity<?, ?, ?, ?, ?, ?, ?> profile, String property, PrismWorkflowConstraint constraint, Errors errors) {
        Collection<?> properties = (Collection<?>) PrismReflectionUtils.getProperty(profile, property);
        validateRangeConstraint(constraint, property, properties.size(), errors);
    }

    private void validateDocumentConstraint(ProfileEntity<?, ?, ?, ?, ?, ?, ?> profile, String propertyDocument, PrismWorkflowConstraint constraint, Errors errors) {
        ProfileDocument<?> documents = profile.getDocument();
        Document document = documents == null ? null : (Document) getProperty(documents, propertyDocument);
        validateRequiredConstraint(document, "document", propertyDocument, constraint, errors);
    }

    private void validateRangeConstraint(PrismWorkflowConstraint constraint, String property, Integer propertiesSize, Errors errors) {
        Integer minimum = constraint.getMinimumPermitted();
        Integer maximum = constraint.getMaximumPermitted();

        if (minimum != null && propertiesSize < minimum) {
            errors.rejectValue(property, "tooFew");
        } else if (maximum != null && propertiesSize > maximum) {
            errors.rejectValue(property, "tooMany");
        }
    }

    private void validateRequiredConstraint(Object object, String parentProperty, String property, PrismWorkflowConstraint constraint, Errors errors) {
        if (isTrue(constraint.isRequired()) && object == null) {
            setValidationMessage(parentProperty, property, errors);
        }
    }

    private void setValidationMessage(String parentProperty, String property, Errors errors) {
        errors.pushNestedPath(parentProperty);
        errors.rejectValue(property, "notNull");
        errors.popNestedPath();
    }

}
