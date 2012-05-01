package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

@RequestMapping("/update")
@Controller
public class ProgrammeDetailsController {

	private static final String STUDENTS_FORM_PROGRAMME_DETAILS_VIEW = "/private/pgStudents/form/components/programme_details";
	private final ApplicationsService applicationsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final ProgrammeDetailsValidator programmeDetailsValidator;
	private final ProgrammeDetailsService programmeDetailsService;
	private final SupervisorJSONPropertyEditor supervisorJSONPropertyEditor;

	ProgrammeDetailsController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public ProgrammeDetailsController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, SupervisorJSONPropertyEditor supervisorJSONPropertyEditor,
			ProgrammeDetailsValidator programmeDetailsValidator, ProgrammeDetailsService programmeDetailsService) {
		this.applicationsService = applicationsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.supervisorJSONPropertyEditor = supervisorJSONPropertyEditor;
		this.programmeDetailsValidator = programmeDetailsValidator;
		this.programmeDetailsService = programmeDetailsService;
	}

	@RequestMapping(value = "/editProgrammeDetails", method = RequestMethod.POST)
	public String editProgrammeDetails(@Valid ProgrammeDetails programmeDetails, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if (programmeDetails.getApplication().isDecided()) {
			throw new CannotUpdateApplicationException();
		}
		if (result.hasErrors()) {
			return STUDENTS_FORM_PROGRAMME_DETAILS_VIEW;
		}
		programmeDetailsService.save(programmeDetails);
		programmeDetails.getApplication().setLastUpdated(new Date());
		applicationsService.save(programmeDetails.getApplication());
		return "redirect:/update/getProgrammeDetails?applicationId=" + programmeDetails.getApplication().getId();

	}

	@RequestMapping(value = "/getProgrammeDetails", method = RequestMethod.GET)
	public String getProgrammeDetailsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_PROGRAMME_DETAILS_VIEW;
	}

	@ModelAttribute("studyOptions")
	public StudyOption[] getStudyOptions(@ModelAttribute ApplicationForm applicationForm) {
		return (StudyOption[]) programmeDetailsService.getAvailableStudyOptions(applicationForm.getProgram()).toArray(new StudyOption[]{});
	}

	@ModelAttribute("referrers")
	public Referrer[] getReferrers() {
		return Referrer.values();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required = false) String message) {
		return message;
	}

	@InitBinder(value = "programmeDetails")
	public void registerPropertyEditors(WebDataBinder binder) {

		binder.setValidator(programmeDetailsValidator);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Supervisor.class, supervisorJSONPropertyEditor);
	}

	@ModelAttribute
	public ProgrammeDetails getProgrammeDetails(@RequestParam Integer applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);

		if (applicationForm.getProgrammeDetails() == null) {
			return new ProgrammeDetails();
		}
		return applicationForm.getProgrammeDetails();

	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

}
