package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private final ProgramDAO programDAO;
	private final ApplicationsService applicationService;
	private final PlainTextUserPropertyEditor userPropertyEditor;
	public static final String PROGRAM_DOES_NOT_EXIST = "private/pgStudents/programs/program_does_not_exist";
	private final ProgramInstanceDAO programInstanceDAO;
	
	ApplicationFormController() {
		this(null, null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProgramDAO programDAO, ApplicationsService applicationService, PlainTextUserPropertyEditor userPropertyEditor,
			ProgramInstanceDAO programInstanceDAO) {
		this.programDAO = programDAO;
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.programInstanceDAO = programInstanceDAO;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView createNewApplicationForm(@RequestParam String program, @RequestParam String programDeadline) throws ParseException {

		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

		Program prog = programDAO.getProgramByCode(program);
		if(prog==null || programInstanceDAO.getActiveProgramInstances(prog).isEmpty()){
			return new ModelAndView(PROGRAM_DOES_NOT_EXIST); 
		}
		ApplicationForm applicationForm = applicationService.createAndSaveNewApplicationForm(user, prog, programDeadline);

		return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
}
