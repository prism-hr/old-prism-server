package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.joda.time.LocalDate;
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
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.CommentAssignedUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.OfferRecommendationService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.OfferRecommendedCommentValidator;

@Controller
@RequestMapping(value = { "/offerRecommendation" })
public class OfferRecommendationController {

    private static final String OFFER_RECOMMENDATION_VIEW_NAME = "private/staff/approver/offer_recommendation_page";

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private OfferRecommendationService offerRecommendedService;

    @Autowired
    private OfferRecommendedCommentValidator offerRecommendedCommentValidator;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private CommentAssignedUserPropertyEditor assignedUserPropertyEditor;

    @RequestMapping(method = RequestMethod.GET)
    public String getOfferRecommendationPage(ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, SystemAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedComment();
        AssignSupervisorsComment approvalComment = (AssignSupervisorsComment) applicationsService.getLatestStateChangeComment(application,
                SystemAction.APPLICATION_COMPLETE_APPROVAL_STAGE);
        if (approvalComment != null) {
            offerRecommendedComment.setPositionTitle(approvalComment.getPositionTitle());
            offerRecommendedComment.setPositionDescription(approvalComment.getPositionDescription());

            LocalDate startDate = approvalComment.getPositionProvisionalStartDate();

            if (!programInstanceService.isPrefferedStartDateWithinBounds(application, startDate)) {
                startDate = programInstanceService.getEarliestPossibleStartDate(application);
            }

            offerRecommendedComment.setPositionProvisionalStartDate(startDate);
            offerRecommendedComment.setAppointmentConditions(approvalComment.getAppointmentConditions());
            offerRecommendedComment.getAssignedUsers().addAll(approvalComment.getAssignedUsers());
        }

        modelMap.put("offerRecommendedComment", offerRecommendedComment);
        // TODO using approvalComment instead of approval round, fix tests and ftl's
        modelMap.put("approvalComment", approvalComment);
        applicationFormUserRoleService.deleteApplicationUpdate(application, user);
        return OFFER_RECOMMENDATION_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String recommendOffer(@Valid @ModelAttribute("offerRecommendedComment") OfferRecommendedComment offerRecommendedComment, BindingResult errors,
            ModelMap modelMap) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, SystemAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (errors.hasErrors()) {
            modelMap.put("offerRecommendedComment", offerRecommendedComment);
            return OFFER_RECOMMENDATION_VIEW_NAME;
        }

        if (offerRecommendedService.moveToApproved(application, offerRecommendedComment)) {
            offerRecommendedService.sendToPortico(application);
            modelMap.put("messageCode", "move.approved");
            modelMap.put("application", application.getApplicationNumber());
            applicationFormUserRoleService.applicationUpdated(application, user);
            return "redirect:/applications";
        } else {
            return "redirect:/rejectApplication?applicationId=" + application.getApplicationNumber() + "&rejectionId=7";
        }

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
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("offerRecommendedComment")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(offerRecommendedCommentValidator);
        binder.registerCustomEditor(CommentAssignedUserPropertyEditor.class, assignedUserPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
    }

    @ModelAttribute("user")
    public User getUser() {
        return getCurrentUser();
    }

    @ModelAttribute("usersInterestedInApplication")
    public List<User> getUsersInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("usersPotentiallyInterestedInApplication")
    public List<User> getUsersPotentiallyInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

}
