package com.zuehlke.pgadmissions.validators;

import static org.apache.commons.lang.BooleanUtils.isNotTrue;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class ProjectDTOValidator extends AbstractValidator {

    public static final String PROSPECTUS_NO_PRIMARY_SUPERVISOR = "prospectus.supervisors.noprimary";
    public static final String PROSPECTUS_NO_SECONDARY_SUPERVISOR = "prospectus.supervisors.nosecondary";
    public static final String PROSPECTUS_SAME_SUPERVISORS_PRIMARY = "prospectus.supervisors.same.primary";
    public static final String PROSPECTUS_SAME_SUPERVISORS_SECONDARY = "prospectus.supervisors.same.secondary";
    public static final String PROSPECTUS_PERSON_NOT_EXISTS = "prospectus.person.not.exists";
    public static final String PROSPECTUS_NO_ADMINISTRATOR = "prospectus.noadministrator";

    private final PersonValidator personValidator;
    private final UserService userService;

    @Autowired
    public ProjectDTOValidator(PersonValidator personValidator, UserService userService) {
        this.personValidator = personValidator;
        this.userService = userService;

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProjectDTO dto = (ProjectDTO) target;
        validateSimpleFields(errors);
        validateStudyDuration(errors, dto.getStudyDuration());
        validateAdministrator(errors, dto.getAdministratorSpecified(), dto.getAdministrator());
        validateClosingDate(errors, dto.getClosingDateSpecified(), dto.getClosingDate());
        validatePrimarySupervisor(errors, dto.getPrimarySupervisor());
        validateSecondarySupervisor(errors, dto.getSecondarySupervisorSpecified(), dto.getSecondarySupervisor());
        validateDifferentSupervisors(errors, dto.getPrimarySupervisor(), dto.getSecondarySupervisor());
    }

    private void validateSimpleFields(Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "program", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "administratorSpecified", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "primarySupervisor", PROSPECTUS_NO_PRIMARY_SUPERVISOR);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "closingDateSpecified", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondarySupervisorSpecified", EMPTY_DROPDOWN_ERROR_MESSAGE);
    }

    private void validateClosingDate(Errors errors, Boolean closingDateSpecified, Date closingDate) {
        if (closingDateSpecified == null || closingDateSpecified == false) {
            return;
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "closingDate", EMPTY_FIELD_ERROR_MESSAGE);
        if (closingDate != null && !closingDate.after(DateUtils.truncateToDay(new Date()))) {
            errors.rejectValue("closingDate", MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE);
        }
    }

    private void validatePrimarySupervisor(Errors errors, Person primarySupervisor) {
        if (primarySupervisor == null) {
            return;
        }
        errors.pushNestedPath("primarySupervisor");
        ValidationUtils.invokeValidator(personValidator, primarySupervisor, errors);
        errors.popNestedPath();
        if (StringUtils.isBlank(primarySupervisor.getEmail())) {
            return;
        }
        User user = userService.getUserByEmailIncludingDisabledAccounts(primarySupervisor.getEmail());
        if (user == null) {
            errors.rejectValue("primarySupervisor", PROSPECTUS_PERSON_NOT_EXISTS);
        }
    }

    private void validateAdministrator(Errors errors, Boolean administratorSpecified, Person administrator) {
        if (isNotTrue(administratorSpecified)) {
            return;
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "administrator", PROSPECTUS_NO_ADMINISTRATOR);
        if (administrator == null) {
            return;
        }
        errors.pushNestedPath("administrator");
        ValidationUtils.invokeValidator(personValidator, administrator, errors);
        errors.popNestedPath();
        if (StringUtils.isBlank(administrator.getEmail())) {
            return;
        }
        User user = userService.getUserByEmailIncludingDisabledAccounts(administrator.getEmail());
        if (user == null) {
            errors.rejectValue("administrator", PROSPECTUS_PERSON_NOT_EXISTS);
        }
    }

    private void validateSecondarySupervisor(Errors errors, Boolean secondarySupervisorSpecified, Person secondarySupervisor) {
        if (isNotTrue(secondarySupervisorSpecified)) {
            return;
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondarySupervisor", PROSPECTUS_NO_SECONDARY_SUPERVISOR);
        if (secondarySupervisor == null) {
            return;
        }
        errors.pushNestedPath("secondarySupervisor");
        ValidationUtils.invokeValidator(personValidator, secondarySupervisor, errors);
        errors.popNestedPath();
        if (StringUtils.isBlank(secondarySupervisor.getEmail())) {
            return;
        }
        User user = userService.getUserByEmailIncludingDisabledAccounts(secondarySupervisor.getEmail());
        if (user == null) {
            errors.rejectValue("secondarySupervisor", PROSPECTUS_PERSON_NOT_EXISTS);
        }
    }

    private void validateDifferentSupervisors(Errors errors, Person primarySupervisor, Person secondarySupervisor) {
        if (primarySupervisor == null || StringUtils.isBlank(primarySupervisor.getEmail()) || secondarySupervisor == null) {
            return;
        }
        if (primarySupervisor.getEmail().equalsIgnoreCase(secondarySupervisor.getEmail())) {
            errors.rejectValue("secondarySupervisor", PROSPECTUS_SAME_SUPERVISORS_SECONDARY);
            errors.rejectValue("primarySupervisor", PROSPECTUS_SAME_SUPERVISORS_PRIMARY);
        }

    }

}