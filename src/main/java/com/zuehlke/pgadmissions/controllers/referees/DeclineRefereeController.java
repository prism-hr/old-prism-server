package com.zuehlke.pgadmissions.controllers.referees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;

@Controller
@RequestMapping("/referee")
public class DeclineRefereeController {
	private static final String DECLINED_VIEW = "private/referees/referee_declined";
	private final RefereeService refereeService;

	DeclineRefereeController(){
		this(null);
	}
	
	@Autowired
	public DeclineRefereeController(RefereeService refereeService) {
		this.refereeService = refereeService;
		
	}
	
	@ModelAttribute
	public Referee getReferee(@RequestParam Integer referee) {	
		Referee ref = refereeService.getRefereeById(referee);
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(ref == null || !currentUser.equals(ref.getUser())){
			throw new ResourceNotFoundException();
		}
		return ref;
	}

	@RequestMapping(value = "/decline", method = RequestMethod.POST)
	public String decline(@ModelAttribute Referee referee) {		
		refereeService.declineToActAsRefereeAndNotifiyApplicant(referee);
		return DECLINED_VIEW;
	}

}
