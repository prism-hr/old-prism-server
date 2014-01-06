package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.security.ActionsProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

@Controller
@RequestMapping("/review")
public class MoveToReviewController {

    public static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
    public static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
    protected final ApplicationsService applicationsService;
    protected final ReviewService reviewService;
    protected final ActionsProvider actionsProvider;
    private final ReviewRoundValidator reviewRoundValidator;
    private final MoveToReviewReviewerPropertyEditor reviewerPropertyEditor;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    MoveToReviewController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public MoveToReviewController(ApplicationsService applicationsService, ReviewService reviewService,
            ReviewRoundValidator reviewRoundValidator, MoveToReviewReviewerPropertyEditor reviewerPropertyEditor,
            final ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.reviewService = reviewService;
        this.actionsProvider = actionsProvider;
        this.reviewRoundValidator = reviewRoundValidator;
        this.reviewerPropertyEditor = reviewerPropertyEditor;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToReview")
    public String getReviewRoundDetailsPage(ModelMap modelMap, @ModelAttribute ApplicationForm application) {
        applicationFormUserRoleService.applicationViewed(application, getCurrentUser());
        return REVIEW_DETAILS_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.GET, value = "reviewersSection")
    public String getReviewersSectionView() {
        return REVIEWERS_SECTION_NAME;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public String moveToReview(@RequestParam String applicationId, @Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, 
    		BindingResult bindingResult, @ModelAttribute ApplicationForm applicationForm) {
    	RegisteredUser initiator = getCurrentUser();
        
    	if (bindingResult.hasErrors()) {
            return REVIEWERS_SECTION_NAME;
        }

        reviewService.moveApplicationToReview(applicationForm, reviewRound, initiator);

        return "/private/common/ajax_OK";
    }
    
    @ModelAttribute("usersInterestedInApplication") 
    public List<RegisteredUser> getUsersInterestedInApplication (@RequestParam String applicationId) {
    	return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }
    
    @ModelAttribute("usersPotentiallyInterestedInApplication") 
    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication (@RequestParam String applicationId) {
    	return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("reviewRound")
    public ReviewRound getReviewRound(@RequestParam String applicationId) {
        ReviewRound reviewRound = new ReviewRound();
        
        List<RegisteredUser> usersInterestedInApplication = getUsersInterestedInApplication(applicationId);
        
        if (usersInterestedInApplication != null) {
	        for (RegisteredUser registeredUser : getUsersInterestedInApplication(applicationId)) {
	        	Reviewer reviewer = new Reviewer();
	        	reviewer.setUser(registeredUser);
	        	reviewRound.getReviewers().add(reviewer);
	        }
        }
        
        return reviewRound;
    }

    @InitBinder(value = "reviewRound")
    public void registerReviewRoundValidator(WebDataBinder binder) {
        binder.setValidator(reviewRoundValidator);
        binder.registerCustomEditor(Reviewer.class, reviewerPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return applicationFormUserRoleService.getCurrentUser();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        actionsProvider.validateAction(application, getCurrentUser(), ApplicationFormAction.ASSIGN_REVIEWERS);
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        return actionsProvider.getApplicationDescriptorForUser(getApplicationForm(applicationId), getCurrentUser());
    }

}