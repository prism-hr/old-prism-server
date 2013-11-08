package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

@Controller
@RequestMapping("/review")
public class MoveToReviewController {

    public static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
    public static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
    protected final ApplicationsService applicationsService;
    protected final UserService userService;
    protected final ReviewService reviewService;
    protected final ActionsProvider actionsProvider;

    private final ReviewRoundValidator reviewRoundValidator;
    private final MoveToReviewReviewerPropertyEditor reviewerPropertyEditor;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    MoveToReviewController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public MoveToReviewController(ApplicationsService applicationsService, UserService userService, ReviewService reviewService,
            ReviewRoundValidator reviewRoundValidator, MoveToReviewReviewerPropertyEditor reviewerPropertyEditor,
            final ApplicationFormUserRoleService applicationFormUserRoleService, ActionsProvider actionsProvider) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.actionsProvider = actionsProvider;
        this.reviewRoundValidator = reviewRoundValidator;
        this.reviewerPropertyEditor = reviewerPropertyEditor;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToReview")
    public String getReviewRoundDetailsPage(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        actionsProvider.validateAction(application, getUser(), ApplicationFormAction.ASSIGN_REVIEWERS);
        applicationFormUserRoleService.deregisterApplicationUpdate(application, getUser());
        return REVIEW_DETAILS_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.GET, value = "reviewersSection")
    public String getReviewersSectionView() {
        return REVIEWERS_SECTION_NAME;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public String moveToReview(@RequestParam String applicationId, 
    		@Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, 
    		BindingResult bindingResult) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);

        actionsProvider.validateAction(applicationForm, getUser(), ApplicationFormAction.ASSIGN_REVIEWERS);
        if (bindingResult.hasErrors()) {
            return REVIEWERS_SECTION_NAME;
        }

        reviewService.moveApplicationToReview(applicationForm, reviewRound);
        applicationFormUserRoleService.movedToReviewStage(reviewRound);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS);

        return "/private/common/ajax_OK";
    }

    @ModelAttribute("reviewRound")
    public ReviewRound getReviewRound(@RequestParam String applicationId) {
        ReviewRound reviewRound = new ReviewRound();
        for (RegisteredUser interestedUser : applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId))) {
        	Reviewer reviewer = new Reviewer();
        	reviewer.setUser(interestedUser);
        	reviewRound.getReviewers().add(reviewer);
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
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("nominatedSupervisors")
    public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
        List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (applicationForm.getLatestReviewRound() == null) {
            nominatedSupervisors.addAll(getOrCreateRegisteredUsersForForm(applicationForm));
        }
        return nominatedSupervisors;
    }

    @ModelAttribute("previousReviewers")
    public List<RegisteredUser> getPreviousReviewers(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        List<RegisteredUser> previousReviewersOfProgram = userService.getAllPreviousReviewersOfProgram(applicationForm.getProgram());
        previousReviewersOfProgram.removeAll(getNominatedSupervisors(applicationId));
        return previousReviewersOfProgram;
    }

    private List<RegisteredUser> getOrCreateRegisteredUsersForForm(ApplicationForm applicationForm) {
        List<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        List<SuggestedSupervisor> suggestedSupervisors = applicationForm.getProgrammeDetails().getSuggestedSupervisors();
        for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
            nominatedSupervisors.add(findOrCreateRegisterUserFromSuggestedSupervisorForForm(suggestedSupervisor, applicationForm));
        }
        return nominatedSupervisors;
    }

    private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
        String supervisorEmail = suggestedSupervisor.getEmail();
        RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
        if (possibleUser == null) {
            possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail, null,
                    applicationForm, Authority.REVIEWER);
        }
        return possibleUser;
    }

}
