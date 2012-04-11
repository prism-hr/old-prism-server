package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgrammeService;
import com.zuehlke.pgadmissions.services.SupervisorService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

@Controller
@RequestMapping("/programme")
public class ProgrammeDetailsController {

	private final ProgrammeService programmeDetailsService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final ProgrammeDetailsValidator programmeDetailsValidator;
	private final SupervisorJSONPropertyEditor supervisorJSONPropertyEditor;
	private final SupervisorService supervisorService;
	
	ProgrammeDetailsController() {
		this(null, null, null, null, null, null);
	}
	
	@Autowired
	public ProgrammeDetailsController(ProgrammeService programmeDetailsService,	
			ApplicationFormPropertyEditor applicationFormPropertyEditor, DatePropertyEditor datePropertyEditor,
			ProgrammeDetailsValidator programmeDetailsValidator, SupervisorJSONPropertyEditor supervisorJSONPropertyEditor, SupervisorService supervisorService) {
		this.programmeDetailsService = programmeDetailsService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.programmeDetailsValidator = programmeDetailsValidator;
		this.supervisorJSONPropertyEditor = supervisorJSONPropertyEditor;
		this.supervisorService = supervisorService;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView editProgrammeDetails(@ModelAttribute("programmeDetails") ProgrammeDetail programmeDetails, BindingResult result) {
		
		
		if (programmeDetails.getApplication() != null && programmeDetails.getApplication().isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		if (programmeDetails.getApplication() != null && programmeDetails.getApplication().getApplicant() != null
				&& !getCurrentUser().equals(programmeDetails.getApplication().getApplicant())) {
			throw new ResourceNotFoundException();
		}
		
		programmeDetailsValidator.validate(programmeDetails, result);
		if(programmeDetails.getPrimarySupervisorId()!=null) {
			Supervisor supervisor = supervisorService.getSupervisorWithId(programmeDetails.getPrimarySupervisorId());
			if(supervisor!=null)
			programmeDetails.setProgrammeDetailsPrimarySupervisor(supervisor);
		}
		if (!result.hasErrors() || programmeDetails.getProgrammeDetailsPrimarySupervisor()!=null) {
			programmeDetailsService.save(programmeDetails);
		}
		
		if (programmeDetails.getApplication() != null) {
			programmeDetails.getApplication().setProgrammeDetails(programmeDetails);
		}
		
		ApplicationPageModel applicationPageModel = new ApplicationPageModel();
		applicationPageModel.setApplicationForm(programmeDetails.getApplication());
		applicationPageModel.setUser(getCurrentUser());
		applicationPageModel.setResult(result);
		applicationPageModel.setStudyOptions(StudyOption.values());
		applicationPageModel.setReferrers(Referrer.values());

		return new ModelAndView("private/pgStudents/form/components/programme_details", "model", applicationPageModel);
	}
	
	@ModelAttribute("programmeDetails")
	public ProgrammeDetail getProgrammeDetails(Integer programmeDetailsId) {
		if (programmeDetailsId == null) {
			return newProgrammeDetail();
		}
		ProgrammeDetail programmeDetails = programmeDetailsService.getProgrammeDetailsById(programmeDetailsId);
		if (programmeDetails == null) {
			throw new ResourceNotFoundException();
		}
		
		return programmeDetails;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	
	ProgrammeDetail newProgrammeDetail() {
		return new ProgrammeDetail();
	}
	
	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(Supervisor.class, supervisorJSONPropertyEditor);
	}


}
