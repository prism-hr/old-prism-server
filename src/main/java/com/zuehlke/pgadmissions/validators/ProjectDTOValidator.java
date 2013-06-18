package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class ProjectDTOValidator extends AbstractValidator {

	public static final String PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER = "prospectus.durationOfStudy.emptyOrNotInteger";
	public static final String PROSPECTUS_NO_PRIMARY_SUPERVISOR= "prospectus.supervisors.noprimary";
	public static final String PROSPECTUS_NO_SECONDARY_SUPERVISOR= "prospectus.supervisors.noprimary";
	public static final String PROSPECTUS_SAME_SUPERVISORS_PRIMARY= "prospectus.supervisors.same.primary";
	public static final String PROSPECTUS_SAME_SUPERVISORS_SECONDARY= "prospectus.supervisors.same.secondary";
	public static final String PROSPECTUS_SUPERVISOR_NOT_EXISTS = "prospectus.supervisors.not.exists";

	private final PersonValidator supervisorValidator;
	private final UserService userService;

	@Autowired
	public ProjectDTOValidator(PersonValidator supervisorValidator, UserService userService){
		this.supervisorValidator = supervisorValidator;
		this.userService = userService;
		
	}

    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProjectDTO dto = (ProjectDTO) target;

        validateRequiredFields(errors);
        validateStudyDuration(errors, dto.getStudyDuration());
        validateClosingDate(errors, dto.getClosingDateSpecified(), dto.getClosingDate());
        validatePrimarySupervisor(errors, dto.getPrimarySupervisor());
        validateSecondarySupervisor(errors, dto.getSecondarySupervisorSpecified(),dto.getSecondarySupervisor());
        validateDifferentSupervisors(errors, dto.getPrimarySupervisor(), dto.getSecondarySupervisor());
    }

	private void validateRequiredFields(Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "program", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
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
        if(closingDate!=null && !closingDate.after(DateUtils.truncateToDay(new Date()))){
    		errors.rejectValue("closingDate", MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE);
        }
	}

	private void validateStudyDuration(Errors errors, Integer studyDuration) {
		if(studyDuration==null){
			return;
		}
    	if(studyDuration == DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY) {
    		errors.rejectValue("studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        } else if (studyDuration.equals(DurationOfStudyPropertyEditor.ERROR_UNIT_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("studyDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
	}

	private void validatePrimarySupervisor(Errors errors, Person primarySupervisor) {
		if(primarySupervisor==null){
			return;
		}
    	errors.pushNestedPath("primarySupervisor");
		ValidationUtils.invokeValidator(supervisorValidator, primarySupervisor, errors);
		if(StringUtils.isBlank(primarySupervisor.getEmail())){
			return;
		}
		errors.popNestedPath();
		RegisteredUser user = userService.getUserByEmailIncludingDisabledAccounts(primarySupervisor.getEmail());
		if( user==null ){
			errors.rejectValue("primarySupervisor", PROSPECTUS_SUPERVISOR_NOT_EXISTS );
		}
	}
	
	private void validateSecondarySupervisor(Errors errors,	Boolean secondarySupervisorSpecified, Person secondarySupervisor) {
		if (secondarySupervisorSpecified == null || secondarySupervisorSpecified == false) {
			return;
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondarySupervisor", PROSPECTUS_NO_SECONDARY_SUPERVISOR);
		if(secondarySupervisor==null){
			return;
		}
		errors.pushNestedPath("secondarySupervisor");
		ValidationUtils.invokeValidator(supervisorValidator, secondarySupervisor, errors);
		if(StringUtils.isBlank(secondarySupervisor.getEmail())){
			return;
		}
		errors.popNestedPath();
		RegisteredUser user = userService.getUserByEmailIncludingDisabledAccounts(secondarySupervisor.getEmail());
		if( user==null ){
			errors.rejectValue("secondarySupervisor", PROSPECTUS_SUPERVISOR_NOT_EXISTS );
		}
	}
	
	private void validateDifferentSupervisors(Errors errors, Person primarySupervisor, Person secondarySupervisor) {
		if(primarySupervisor == null || StringUtils.isBlank(primarySupervisor.getEmail()) || secondarySupervisor == null){
			return;
		}
		if(primarySupervisor.getEmail().equalsIgnoreCase(secondarySupervisor.getEmail())){
			errors.rejectValue("secondarySupervisor", PROSPECTUS_SAME_SUPERVISORS_SECONDARY );
			errors.rejectValue("primarySupervisor", PROSPECTUS_SAME_SUPERVISORS_PRIMARY );
		}
		
	}
	
}
