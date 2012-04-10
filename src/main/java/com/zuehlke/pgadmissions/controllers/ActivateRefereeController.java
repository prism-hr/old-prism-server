package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
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

	ActivateRefereeController() {
		this(null, null);
	}

	@Autowired
	public ActivateRefereeController(RefereeService refereeService, ApplicationPageModelBuilder applicationPageModelBuilder) {
		this.refereeService = refereeService;
		this.applicationPageModelBuilder = applicationPageModelBuilder;
	}

	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public ModelAndView getReferencesPage(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationPageModel model = new ApplicationPageModel();
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
	
	@RequestMapping(value = "/register",method = RequestMethod.GET)
	public ModelAndView getRefereeRegisterPage(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		RegisteredUser refereeUser = referee.getUser();
		ApplicationPageModel model = new ApplicationPageModel();
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
			model.setApplicationForm(applicationForm);
			model.setReferee(referee);
			model.setRefereeUser(refereeUser);
		}
		return new ModelAndView(REGISTER_REFEREE_VIEW_NAME, "model", model);
	}
	
	@RequestMapping(value = "/application", method = RequestMethod.GET)
	public ModelAndView getViewApplicationPageForReferee(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationForm application = referee.getApplication();
		if (application.isDecided()) {
			return new ModelAndView(EXPIRED_VIEW_NAME);
		}
		return new ModelAndView(VIEW_APPLICATION_INTERNAL_VIEW_NAME, "model", applicationPageModelBuilder.createAndPopulatePageModel(application, null, null, null, null));
	}
}
