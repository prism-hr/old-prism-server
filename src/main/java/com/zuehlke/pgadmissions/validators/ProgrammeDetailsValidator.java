package com.zuehlke.pgadmissions.validators;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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
public class ProgrammeDetailsValidator extends FormSectionObjectValidator implements Validator  {

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
	public void addExtraValidation(final Object target, final Errors errors) {
		super.addExtraValidation(target, errors);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourcesOfInterest", "dropdown.radio.select.none");

		ProgrammeDetails programmeDetail = (ProgrammeDetails) target;

		if (programmeDetail.getSourcesOfInterest() != null && programmeDetail.getSourcesOfInterest().isFreeText()) {
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourcesOfInterestText", "text.field.empty");
		}
		
		List<ProgramInstance> programInstances = programInstaceDAO.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(programmeDetail.getApplication().getProgram(), programmeDetail.getStudyOption());
		if (programInstances == null || programInstances.isEmpty()) {
			errors.rejectValue("studyOption", "programmeDetails.studyOption.invalid");
		}
		
		if (programInstances != null && !programInstances.isEmpty() && programmeDetail.getStartDate() != null) {
		    DateTime startDateFirstProgrameInstance = new DateTime(programInstances.get(0).getApplicationStartDate());
		    DateTime derivedEndDateLastProgrameInstance = new DateTime(programInstances.get(programInstances.size()-1).getApplicationDeadline()).plusYears(1);
		    DateTime userEnteredPreferredStartDate = new DateTime(programmeDetail.getStartDate());
		
		    if (userEnteredPreferredStartDate.isBefore(startDateFirstProgrameInstance) || userEnteredPreferredStartDate.isAfter(derivedEndDateLastProgrameInstance)) {
		        errors.rejectValue("startDate", "programmeDetails.startDate.invalid", new Object[] {startDateFirstProgrameInstance.toString("dd-MMM-yyyy"), derivedEndDateLastProgrameInstance.toString("dd-MMM-yyyy")}, "");
		    }
		} else if(programmeDetail.getStartDate() != null && programmeDetail.getStartDate().before(new Date())) {
            errors.rejectValue("startDate", "date.field.notfuture");
        }
		
		List<SuggestedSupervisor> supervisors = programmeDetail.getSuggestedSupervisors();
		for (int i = 0; i < supervisors.size(); i++) {
			if (StringUtils.isBlank(supervisors.get(i).getFirstname())) {
				errors.rejectValue("suggestedSupervisors", "text.field.empty");
			}
			if (StringUtils.isBlank(supervisors.get(i).getLastname())) {
				errors.rejectValue("suggestedSupervisors", "text.field.empty");
			}
			if (StringUtils.isBlank(supervisors.get(i).getEmail())) {
                errors.rejectValue("suggestedSupervisors", "text.field.empty");
            }
		}
	}
}
