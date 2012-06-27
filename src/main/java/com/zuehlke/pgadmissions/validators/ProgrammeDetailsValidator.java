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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "referrer", "dropdown.radio.select.none");

		ProgrammeDetails programmeDetail = (ProgrammeDetails) target;

		List<ProgramInstance> programInstances = programInstaceDAO.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetail.getApplication()
				.getProgram(), programmeDetail.getStudyOption());
		if (programInstances == null || programInstances.isEmpty()) {
			errors.rejectValue("studyOption", "programmeDetails.studyOption.invalid");
		}

		if(programmeDetail.getStartDate() != null && programmeDetail.getStartDate().before(new Date())){
			errors.rejectValue("startDate", "date.field.notfuture");
		}
		List<SuggestedSupervisor> supervisors = programmeDetail.getSuggestedSupervisors();
		for (int i = 0; i < supervisors.size(); i++) {
			if (supervisors.get(i).getFirstname() == "" || supervisors.get(i).getFirstname() == null) {
				errors.rejectValue("suggestedSupervisors", "text.field.empty");
			}
			if (supervisors.get(i).getLastname() == "" || supervisors.get(i).getLastname() == null) {
				errors.rejectValue("suggestedSupervisors", "text.field.empty");
			}
		}
	}

}
