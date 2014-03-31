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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.CommentAssignedUserPropertyEditor;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/review")
public class MoveToReviewController {
    // TODO change reviewRound to assignReviewersComment, fix tests

    public static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
    public static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";
    
    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActionsProvider actionsProvider;

    @Autowired
    private CommentAssignedUserPropertyEditor assignedUserPropertyEditor;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;


    @RequestMapping(method = RequestMethod.GET, value = "moveToReview")
    public String getReviewRoundDetailsPage(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        actionsProvider.validateAction(application, getUser(), ApplicationFormAction.ASSIGN_REVIEWERS);
        applicationFormUserRoleService.deleteApplicationUpdate(application, getUser());
        return REVIEW_DETAILS_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.GET, value = "reviewersSection")
    public String getReviewersSectionView() {
        return REVIEWERS_SECTION_NAME;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public String moveToReview(@RequestParam String applicationId, @Valid @ModelAttribute("reviewRound") AssignReviewersComment assignReviewersComment, BindingResult bindingResult) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);

        RegisteredUser user = getUser();

        actionsProvider.validateAction(applicationForm, getUser(), ApplicationFormAction.ASSIGN_REVIEWERS);
        if (bindingResult.hasErrors()) {
            return REVIEWERS_SECTION_NAME;
        }

        assignReviewersComment.setUser(user);
        assignReviewersComment.setApplication(applicationForm);
        reviewService.moveApplicationToReview(applicationForm, assignReviewersComment);

        return "/private/common/ajax_OK";
    }

    @ModelAttribute("usersInterestedInApplication")
    public List<RegisteredUser> getUsersInterestedInApplication(@RequestParam String applicationId) {
        // FIXME isReviewerInReviewRound method has been removed from RegisteredUser class, provide this information in other way (by moving the method into
        // aservice, or this method can return a map)
        return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("usersPotentiallyInterestedInApplication")
    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("assignReviewersComment")
    public AssignReviewersComment getAssignReviewersComment(@RequestParam String applicationId) {
        AssignReviewersComment assignReviewersComment = new AssignReviewersComment();

        List<RegisteredUser> usersInterestedInApplication = getUsersInterestedInApplication(applicationId);

        if (usersInterestedInApplication != null) {
            for (RegisteredUser registeredUser : getUsersInterestedInApplication(applicationId)) {
                CommentAssignedUser reviewer = new CommentAssignedUser();
                reviewer.setUser(registeredUser);
                assignReviewersComment.getAssignedUsers().add(reviewer);
            }
        }

        return assignReviewersComment;
    }

    @InitBinder(value = "reviewRound")
    public void registerReviewRoundValidator(WebDataBinder binder) {
//        binder.setValidator(reviewRoundValidator);
        binder.registerCustomEditor(CommentAssignedUser.class, assignedUserPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getByApplicationNumber(applicationId);
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

}