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
	

	private final RefereeService refereeService;
	
	private final ApplicationsService applicationsService;

	ActivateRefereeController() {
		this(null, null, null);
	}

	@Autowired
	public ActivateRefereeController(RefereeService refereeService, ApplicationPageModelBuilder applicationPageModelBuilder, ApplicationsService applicationsService) {
		this.refereeService = refereeService;
	
		this.applicationsService = applicationsService;
	}

	
	@RequestMapping(value = "/addReferences",method = RequestMethod.GET)
	public ModelAndView getUploadReferencesPage(@RequestParam Integer application, ModelMap modelMap) {
		
		RegisteredUser currentUser = getCurrentUser();
		ApplicationForm form = applicationsService.getApplicationById(application);
		if(form == null || ( !currentUser.isRefereeOfApplicationForm(form) && !currentUser.isInRole(Authority.SUPERADMINISTRATOR) )){
			throw new ResourceNotFoundException();
		}		
		modelMap.put("application", form);		
		modelMap.put("user", currentUser);
		modelMap.put("referee", refereeService.getRefereeByUserAndApplication(currentUser, form));
		if(form.isDecided()){
			return new ModelAndView(EXPIRED_VIEW_NAME, modelMap);	
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, modelMap);

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

}
