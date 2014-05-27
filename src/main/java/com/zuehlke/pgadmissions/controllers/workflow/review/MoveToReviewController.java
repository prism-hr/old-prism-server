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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.CommentAssignedUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;

@Controller
@RequestMapping("/review")
public class MoveToReviewController {
    // TODO change reviewRound to assignReviewersComment, fix tests

    public static final String REVIEW_DETAILS_VIEW_NAME = "/private/staff/reviewer/assign_reviewers_to_appl_page";
    public static final String REVIEWERS_SECTION_NAME = "/private/staff/reviewer/assign_reviewers_section";

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentAssignedUserPropertyEditor assignedUserPropertyEditor;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @RequestMapping(method = RequestMethod.GET, value = "moveToReview")
    public String getReviewRoundDetailsPage(ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        actionService.validateAction(application, getUser(), SystemAction.APPLICATION_ASSIGN_REVIEWERS);
        applicationFormUserRoleService.deleteApplicationUpdate(application, getUser());
        return REVIEW_DETAILS_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.GET, value = "reviewersSection")
    public String getReviewersSectionView(ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        modelMap.put("comment", createAssignReviewersComment(application.getCode()));
        return REVIEWERS_SECTION_NAME;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public String moveToReview(@Valid AssignReviewersComment comment, BindingResult bindingResult, ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = getUser();

        actionService.validateAction(application, user, SystemAction.APPLICATION_ASSIGN_REVIEWERS);
        if (bindingResult.hasErrors()) {
            modelMap.put("comment", comment);
            return REVIEWERS_SECTION_NAME;
        }

        reviewService.moveApplicationToReview(application.getId(), comment);

        return "/private/common/ajax_OK";
    }

    @ModelAttribute("usersInterestedInApplication")
    public List<User> getUsersInterestedInApplication(@RequestParam String applicationId) {
        // FIXME isReviewerInReviewRound method has been removed from RegisteredUser class, provide this information in other way (by moving the method into
        // aservice, or this method can return a map)
        return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("usersPotentiallyInterestedInApplication")
    public List<User> getUsersPotentiallyInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

    protected AssignReviewersComment createAssignReviewersComment(@RequestParam String applicationId) {
        AssignReviewersComment assignReviewersComment = new AssignReviewersComment();
        
        for (User user : getUsersInterestedInApplication(applicationId)) {
            assignReviewersComment.getAssignedUsers().add(new CommentAssignedUser().withUser(user));
        }
        
        return assignReviewersComment;
    }

    @InitBinder(value = "reviewRound")
    public void registerReviewRoundValidator(WebDataBinder binder) {
        // binder.setValidator(reviewRoundValidator);
        binder.registerCustomEditor(CommentAssignedUser.class, assignedUserPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public Application getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

}