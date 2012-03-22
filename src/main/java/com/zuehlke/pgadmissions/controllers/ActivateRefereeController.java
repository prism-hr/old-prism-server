package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.RefereeService;


@Controller
@RequestMapping("/references")
public class ActivateRefereeController {

	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	
	private RefereeService refereeService;
	
	ActivateRefereeController() {
		this(null);
	}
	
	@Autowired
	public ActivateRefereeController(RefereeService refereeService){
		this.refereeService = refereeService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getReferencesPage(@RequestParam String activationCode) {
		Referee referee = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationPageModel model = new ApplicationPageModel();
		if(referee == null || referee.getApplication()==null){
			throw new ResourceNotFoundException();
		}
		else{
			ApplicationForm applicationForm = referee.getApplication();
			model.setApplicationForm(applicationForm);
			model.setReferee(referee);
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
	}
	
}
