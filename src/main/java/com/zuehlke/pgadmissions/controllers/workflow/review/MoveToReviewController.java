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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;
@Controller
@RequestMapping("/review")
public class MoveToReviewController extends ReviewController {

	
	MoveToReviewController() {
		this(null, null,null, null, null, null, null, null);
	}
		
	@Autowired
	public MoveToReviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator reviewerValidator, ReviewRoundValidator reviewRoundValidator,
			ReviewService reviewService, MessageSource messageSource, ReviewerPropertyEditor reviewerPropertyEditor,  EncryptionHelper encryptionHelper) {
		super(applicationsService, userService, reviewerValidator,reviewRoundValidator, reviewService, messageSource,   reviewerPropertyEditor, encryptionHelper);
		
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToReview")
	public String getReviewRoundDetailsPage(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return REVIEW_DETAILS_VIEW_NAME;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToReview(@RequestParam String applicationId, @Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, BindingResult bindingResult, ModelMap modelMap) {

		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return REVIEW_DETAILS_VIEW_NAME;
		}
		reviewService.moveApplicationToReview( applicationForm, reviewRound);		
		return "redirect:/applications?messageCode=move.review&application=" + applicationForm.getApplicationNumber();
	}
	
	
	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam Object applicationId) {
		return new ReviewRound();
	}


}
