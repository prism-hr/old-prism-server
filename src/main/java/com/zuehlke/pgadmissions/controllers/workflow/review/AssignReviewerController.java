package com.zuehlke.pgadmissions.controllers.workflow.review;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

@Controller
@RequestMapping("/review")
public class AssignReviewerController extends ReviewController {

	AssignReviewerController(){
		this(null, null, null, null,null, null, null);
	}
	@Autowired
	public AssignReviewerController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator reviewerValidator, ReviewRoundValidator reviewRoundValidator,
			ReviewService reviewService, MessageSource messageSource, ReviewerPropertyEditor reviewerPropertyEditor) {
		super(applicationsService, userService, reviewerValidator, reviewRoundValidator, reviewService, messageSource, reviewerPropertyEditor);
	}

	@RequestMapping(method = RequestMethod.GET, value = "assignReviewers")
	public String getAssignReviewersPage(ModelMap modelMap) {
		modelMap.put("assignOnly", true);
		return REVIEW_DETAILS_VIEW_NAME;
	}

	@Override
	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam Integer applicationId) {
		return getApplicationForm(applicationId).getLatestReviewRound();

	}
	
	@RequestMapping(value = "/assign", method = RequestMethod.POST)
	public String assignReviewers(@Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, BindingResult bindingResult) {
		if(bindingResult.hasErrors()){
			return REVIEW_DETAILS_VIEW_NAME;
		}
		reviewService.save(reviewRound);
		return "redirect:/applications";
	}


}
