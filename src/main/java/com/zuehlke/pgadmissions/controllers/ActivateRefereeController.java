package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.ApplicationPageModelBuilder;

@Controller
@RequestMapping("/referee")
public class ActivateRefereeController {

	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	private static final String REGISTER_REFEREE_VIEW_NAME = "private/referees/register_referee";
	private static final String EXPIRED_VIEW_NAME = "private/referees/upload_references_expired";
	private static final String ALREADY_REGISTERED = "private/referees/already_registered";
	private static final String VIEW_APPLICATION_INTERNAL_VIEW_NAME = "private/referees/application/main_application_page";

	private final RefereeService refereeService;
	private final ApplicationPageModelBuilder applicationPageModelBuilder;
	private final ApplicationsService applicationsService;

	ActivateRefereeController() {
		this(null, null, null);
	}

	@Autowired
	public ActivateRefereeController(RefereeService refereeService, ApplicationPageModelBuilder applicationPageModelBuilder, ApplicationsService applicationsService) {
		this.refereeService = refereeService;
		this.applicationPageModelBuilder = applicationPageModelBuilder;
		this.applicationsService = applicationsService;
	}

	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public ModelAndView getReferencesPage(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationPageModel model = new ApplicationPageModel();
		//check if current user can upload
		if (referee == null || referee.getApplication() == null) {
			throw new ResourceNotFoundException();
		} else {
			ApplicationForm applicationForm = referee.getApplication();
			if (applicationForm.isDecided()) {
				return new ModelAndView(EXPIRED_VIEW_NAME);
			}
			model.setApplicationForm(applicationForm);
			model.setReferee(referee);
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
	}
	
	@RequestMapping(value = "/addReferences",method = RequestMethod.GET)
	public ModelAndView getUploadReferencesPage(@RequestParam Integer applicationFormId) {
		ApplicationForm form = applicationsService.getApplicationById(applicationFormId);
		ApplicationPageModel model = new ApplicationPageModel();
		RegisteredUser user = getCurrentUser();
		if (form == null || form.isDecided() || !user.isInRole(Authority.REFEREE)) {
			throw new ResourceNotFoundException();
		} else {
			if (form.isDecided()) {
				return new ModelAndView(EXPIRED_VIEW_NAME);
			}
			model.setApplicationForm(form);
			Referee referee = refereeService.getRefereeByUserAndApplication(user, form);
			model.setReferee(referee);
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	
	@RequestMapping(value = "/register",method = RequestMethod.GET)
	public ModelAndView getRefereeRegisterPage(@RequestParam String activationCode, ModelMap modelMap) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		RegisteredUser refereeUser = referee.getUser();
		if (referee == null || referee.getApplication() == null || refereeUser == null) {
			throw new ResourceNotFoundException();
		} else {
			ApplicationForm applicationForm = referee.getApplication();
			if (applicationForm.isDecided()) {
				return new ModelAndView(EXPIRED_VIEW_NAME);
			}
			if(refereeUser.isEnabled()){
				return new ModelAndView(ALREADY_REGISTERED);
			}
			refereeUser.setCurrentReferee(referee);
			modelMap.put("referee", refereeUser);
			
		}
		return new ModelAndView(REGISTER_REFEREE_VIEW_NAME, modelMap);
	}
	
	@RequestMapping(value = "/application", method = RequestMethod.GET)
	public ModelAndView getViewApplicationPageForReferee(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationForm application = referee.getApplication();
		if (application.isDecided()) {
			return new ModelAndView(EXPIRED_VIEW_NAME);
		}
		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "applicationForm", application);
	}
}
