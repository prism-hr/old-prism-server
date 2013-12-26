package com.zuehlke.pgadmissions.controllers.workflow.approval;

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
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/approval")
public class CreateNewSupervisorController {
	private static final String CREATE_SUPERVISOR_SECTION = "/private/staff/supervisors/create_supervisor_section";
	private static final String JSON_VIEW = "/private/staff/reviewer/reviewer_json";
	private final UserService userService;
	private final ApplicationsService applicationsService;
	private final NewUserByAdminValidator supervisorValidator;

	CreateNewSupervisorController() {
		this(null, null, null);
	}

	@Autowired
	public CreateNewSupervisorController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator supervisorValidator) {
				this.applicationsService = applicationsService;
				this.userService = userService;
				this.supervisorValidator = supervisorValidator;
	}

	@RequestMapping(value = "/createSupervisor", method = RequestMethod.POST)
	public ModelAndView createNewSupervisorUser(@Valid @ModelAttribute("supervisor") RegisteredUser suggestedNewSupervisorUser, BindingResult bindingResult, @ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView(CREATE_SUPERVISOR_SECTION);
		}
		ModelAndView modelAndView = new ModelAndView(JSON_VIEW);
		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(suggestedNewSupervisorUser.getEmail());
		if (existingUser != null) {
			modelAndView.getModel().put("isNew", false);		
			modelAndView.getModel().put("user", existingUser);
			return modelAndView;
		}
		
		modelAndView.getModel().put("isNew", true);
		RegisteredUser newUser = userService.createNewUserInRoles(suggestedNewSupervisorUser.getFirstName(), suggestedNewSupervisorUser.getLastName(), suggestedNewSupervisorUser.getEmail(), Authority.SUPERVISOR);
		modelAndView.getModel().put("user", newUser);
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "create_supervisor_section")
	public String getCreateSupervisorSection() {
			return CREATE_SUPERVISOR_SECTION;
	}
	
	@ModelAttribute("supervisor")
	public RegisteredUser getSupervisor() {
		return new RegisteredUser();
	}
	
	@InitBinder(value = "supervisor")
	public void registerSupervisorValidators(WebDataBinder binder) {
		binder.setValidator(supervisorValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }
        
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
	}
	
	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null) {
			throw new ResourceNotFoundException();
		}
		return application;
	}
}