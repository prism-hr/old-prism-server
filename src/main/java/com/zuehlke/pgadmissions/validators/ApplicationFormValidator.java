package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

@Component
public class ApplicationFormValidator implements Validator{

	private final ProgramInstanceDAO programInstanceDAO;

	ApplicationFormValidator(){
		this(null);
	}
	
	@Autowired
	public ApplicationFormValidator(ProgramInstanceDAO programInstanceDAO) {
		this.programInstanceDAO = programInstanceDAO;
		
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationForm applicationForm = (ApplicationForm) target;
		ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
		if(programmeDetails != null && programmeDetails.getId() == null){
			errors.rejectValue("programmeDetails", "user.programmeDetails.incomplete");
		}else{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetails", "user.programmeDetails.incomplete");
		}
		if(applicationForm.getPersonalDetails() != null && applicationForm.getPersonalDetails().getId() == null){
			errors.rejectValue( "personalDetails", "user.personalDetails.incomplete");
		}else{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalDetails", "user.personalDetails.incomplete");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "documents.section.invalid");
		if(applicationForm.getReferees().size() < 3){
			errors.rejectValue("referees", "user.referees.notvalid");
		}
		
		if (applicationForm.getAdditionalInformation() != null ) {
			if (applicationForm.getAdditionalInformation().length() > 5000) {
				errors.rejectValue("additionalInformation", "user.additionalInformation.notvalid");
			}
		}
		
		if(programmeDetails != null){
			List<ProgramInstance> programInstances = programInstanceDAO.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(applicationForm.getProgram(), programmeDetails.getStudyOption());
			if(programInstances==null || programInstances.isEmpty()){
				errors.rejectValue("programmeDetails", "programmeDetails.studyOption.invalid");
			}
		}
	
	}
}
