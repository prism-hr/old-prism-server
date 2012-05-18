package com.zuehlke.pgadmissions.controllers.workflow.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/review")
public class AddReviewersController extends AssignReviewerController {

	AddReviewersController() {
		this(null, null, null, null, null);		
	}
	

	@Autowired
	public AddReviewersController(ApplicationsService applicationService, ReviewService reviewService, UserService userService,
			NewUserByAdminValidator validator, MessageSource msgSource) {
		super(applicationService, reviewService, userService, validator, msgSource);
	
	}


	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam(required = false) Integer applicationId) {
		return applicationService.getApplicationById(applicationId).getLatestReviewRound();
	}

	@RequestMapping(method = RequestMethod.GET, value = "assignReviewers")
	public String getAddReviewsPage(ModelMap modelMap) {
		modelMap.put("assignOnly", true);
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

}
