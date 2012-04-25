package com.zuehlke.pgadmissions.controllers;

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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private final ProgramDAO programDAO;
	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;

	ApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProgramDAO programDAO, ApplicationsService applicationService, UserPropertyEditor userPropertyEditor) {
		this.programDAO = programDAO;
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView createNewApplicationForm(@RequestParam Integer program) {

		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

		Program prog = programDAO.getProgramById(program);
		ApplicationForm applicationForm = applicationService.createAndSaveNewApplicationForm(user, prog);

		return new ModelAndView("redirect:/application", "applicationId", applicationForm.getId());

	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
}
