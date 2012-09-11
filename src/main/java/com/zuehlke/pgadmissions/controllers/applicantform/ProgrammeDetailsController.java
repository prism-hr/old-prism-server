package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
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
	private final SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor;
	private final UserService userService;

	ProgrammeDetailsController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public ProgrammeDetailsController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor,
			ProgrammeDetailsValidator programmeDetailsValidator, ProgrammeDetailsService programmeDetailsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.supervisorJSONPropertyEditor = supervisorJSONPropertyEditor;
		this.programmeDetailsValidator = programmeDetailsValidator;
		this.programmeDetailsService = programmeDetailsService;
		this.userService = userService;
	}

	@RequestMapping(value = "/editProgrammeDetails", method = RequestMethod.POST)
	public String editProgrammeDetails( @Valid ProgrammeDetails programmeDetails, BindingResult result) {
		
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
		return "redirect:/update/getProgrammeDetails?applicationId=" + programmeDetails.getApplication().getApplicationNumber();

	}

	@RequestMapping(value = "/getProgrammeDetails", method = RequestMethod.GET)
	public String getProgrammeDetailsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_PROGRAMME_DETAILS_VIEW;
	}

	@ModelAttribute("studyOptions")
	public StudyOption[] getStudyOptions(@RequestParam String applicationId) {
		return (StudyOption[]) programmeDetailsService.getAvailableStudyOptions(getApplicationForm(applicationId).getProgram()).toArray(new StudyOption[] {});
	}

	@ModelAttribute("referrers")
	public Referrer[] getReferrers() {
		return Referrer.values();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
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
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditor);
	}
	
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

	@ModelAttribute
	public ProgrammeDetails getProgrammeDetails(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);

		if (applicationForm.getProgrammeDetails() == null) {
			return new ProgrammeDetails();
		}
		return applicationForm.getProgrammeDetails();

	}

	private RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

}
