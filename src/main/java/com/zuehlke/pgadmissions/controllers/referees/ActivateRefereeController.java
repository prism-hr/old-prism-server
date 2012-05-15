package com.zuehlke.pgadmissions.controllers.referees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;

@Controller
@RequestMapping("/referee")
public class ActivateRefereeController {

	private static final String REGISTER_REFEREE_VIEW_NAME = "private/referees/register_referee";
	private static final String EXPIRED_VIEW_NAME = "private/referees/upload_references_expired";
	private static final String ALREADY_REGISTERED = "private/referees/already_registered";

	private final RefereeService refereeService;


	ActivateRefereeController() {
		this(null);
	}

	@Autowired
	public ActivateRefereeController(RefereeService refereeService) {
		this.refereeService = refereeService;

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
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
			if (refereeUser.isEnabled()) {
				return new ModelAndView(ALREADY_REGISTERED);
			}
			refereeUser.setCurrentReferee(referee);
			modelMap.put("referee", refereeUser);

		}
		return new ModelAndView(REGISTER_REFEREE_VIEW_NAME, modelMap);
	}

}
