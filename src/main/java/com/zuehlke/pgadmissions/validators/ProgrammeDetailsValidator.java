package com.zuehlke.pgadmissions.validators;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

@Component
public class ProgrammeDetailsValidator implements Validator {

	private final ProgramInstanceDAO programInstaceDAO;

	ProgrammeDetailsValidator() {
		this(null);
	}

	@Autowired
	public ProgrammeDetailsValidator(ProgramInstanceDAO programInstaceDAO) {
		this.programInstaceDAO = programInstaceDAO;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ProgrammeDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", "user.programmeName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", "user.studyOption.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "user.programmeStartDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "referrer", "user.programmeReferrer.notempty");

		ProgrammeDetails programmeDetail = (ProgrammeDetails) target;

		List<ProgramInstance> programInstances = programInstaceDAO.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetail.getApplication()
				.getProgram(), programmeDetail.getStudyOption());
		if (programInstances == null || programInstances.isEmpty()) {
			errors.rejectValue("studyOption", "programmeDetails.studyOption.invalid");
		}

		if(programmeDetail.getStartDate() != null && programmeDetail.getStartDate().before(new Date())){
			errors.rejectValue("startDate", "user.programmeStartDate.notFuture");
		}
		List<SuggestedSupervisor> supervisors = programmeDetail.getSuggestedSupervisors();
		for (int i = 0; i < supervisors.size(); i++) {
			// if
			// (!EmailValidator.getInstance().isValid(supervisors.get(i).getEmail()))
			// {
			// errors.rejectValue("supervisors",
			// "programmeDetails.email.invalid");
			// }
			if (supervisors.get(i).getFirstname() == "" || supervisors.get(i).getFirstname() == null) {
				errors.rejectValue("suggestedSupervisors", "programmeDetails.firstname.notempty");
			}
			if (supervisors.get(i).getLastname() == "" || supervisors.get(i).getLastname() == null) {
				errors.rejectValue("suggestedSupervisors", "programmeDetails.lastname.notempty");
			}
		}
	}

}
