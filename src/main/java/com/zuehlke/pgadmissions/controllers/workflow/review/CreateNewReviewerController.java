package com.zuehlke.pgadmissions.controllers.workflow.review;

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
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/review")
public class CreateNewReviewerController {

	private static final String CREATE_REVIEWER_SECTION = "/private/staff/reviewer/create_reviewer_section";
	private static final String JSON_VIEW = "/private/staff/reviewer/reviewer_json";
	private final ApplicationFormUserRoleService applicationFormUserRoleService;
	private final ApplicationsService applicationsService;
	private final NewUserByAdminValidator reviewerValidator;

	CreateNewReviewerController() {
		this(null, null, null);
	}

	@Autowired
	public CreateNewReviewerController(ApplicationsService applicationsService, ApplicationFormUserRoleService applicationFormUserRoleService, NewUserByAdminValidator reviewerValidator) {
				this.applicationsService = applicationsService;
				this.applicationFormUserRoleService= applicationFormUserRoleService;
				this.reviewerValidator = reviewerValidator;
	}

	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public ModelAndView createNewReviewerUser(@Valid @ModelAttribute("reviewer") RegisteredUser suggestedNewReviewerUser, BindingResult bindingResult,
			@ModelAttribute("applicationForm") ApplicationForm applicationForm) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("/private/staff/reviewer/create_reviewer_section");
		}
		
		ModelAndView modelAndView = new ModelAndView(JSON_VIEW);
		RegisteredUser userToAssign = applicationFormUserRoleService.createRegisteredUser(suggestedNewReviewerUser.getFirstName(), suggestedNewReviewerUser.getLastName(), suggestedNewReviewerUser.getEmail());
		modelAndView.getModel().put("isNew", applicationFormUserRoleService.isNewlyCreatedUser(userToAssign));		
		modelAndView.getModel().put("user", userToAssign);
		return modelAndView;
	}


	@RequestMapping(method = RequestMethod.GET, value = "create_reviewer_section")
	public String getCreateReviewerSection() {
			return CREATE_REVIEWER_SECTION;
	}
	
	@ModelAttribute("reviewer")
	public RegisteredUser getReviewer() {
		return new RegisteredUser();
	}
	
	@InitBinder(value = "reviewer")
	public void registerReviewerValidators(WebDataBinder binder) {
		binder.setValidator(reviewerValidator);
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