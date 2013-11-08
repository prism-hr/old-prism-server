package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION;

import java.util.Date;
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
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.OfferRecommendationService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.SupervisorsProvider;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OfferRecommendedCommentValidator;

@Controller
@RequestMapping(value = { "/offerRecommendation" })
public class OfferRecommendationController {

    private static final String OFFER_RECOMMENDATION_VIEW_NAME = "private/staff/approver/offer_recommendation_page";

    private final UserService userService;

    private final ApplicationsService applicationsService;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    private final ActionsProvider actionsProvider;

    private final OfferRecommendationService offerRecommendedService;

    private final OfferRecommendedCommentValidator offerRecommendedCommentValidator;

    private final DatePropertyEditor datePropertyEditor;

    private final ProgramInstanceService programInstanceService;

    private final SupervisorsProvider supervisorsProvider;

    private final SupervisorPropertyEditor supervisorPropertyEditor;

    public OfferRecommendationController() {
        this(null, null, null, null, null,  null, null, null, null, null);
    }

    @Autowired
    public OfferRecommendationController(ApplicationsService applicationsService, UserService userService, ActionsProvider actionsProvider,
            ApplicationFormUserRoleService applicationFormUserRoleService,  
            OfferRecommendationService offerRecommendedService, OfferRecommendedCommentValidator offerRecommendedCommentValidator,
            DatePropertyEditor datePropertyEditor, ProgramInstanceService programInstanceService, SupervisorsProvider supervisorsProvider,
            SupervisorPropertyEditor supervisorPropertyEditor) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.actionsProvider = actionsProvider;
        this.offerRecommendedService = offerRecommendedService;
        this.offerRecommendedCommentValidator = offerRecommendedCommentValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programInstanceService = programInstanceService;
        this.supervisorsProvider = supervisorsProvider;
        this.supervisorPropertyEditor = supervisorPropertyEditor;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getOfferRecommendationPage(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(application, user, CONFIRM_OFFER_RECOMMENDATION);

        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedComment();
        ApprovalRound approvalRound = application.getLatestApprovalRound();
        if (approvalRound != null) {
            offerRecommendedComment.setProjectTitle(approvalRound.getProjectTitle());
            offerRecommendedComment.setProjectAbstract(approvalRound.getProjectAbstract());

            Date startDate = approvalRound.getRecommendedStartDate();

            if (!programInstanceService.isPrefferedStartDateWithinBounds(application, startDate)) {
                startDate = programInstanceService.getEarliestPossibleStartDate(application);
            }

            offerRecommendedComment.setRecommendedStartDate(startDate);
            offerRecommendedComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
            offerRecommendedComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
            offerRecommendedComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
            offerRecommendedComment.getSupervisors().addAll(approvalRound.getSupervisors());
        }
        modelMap.put("offerRecommendedComment", offerRecommendedComment);
        applicationFormUserRoleService.deregisterApplicationUpdate(application, user);
        return OFFER_RECOMMENDATION_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String recommendOffer(@Valid @ModelAttribute("offerRecommendedComment") OfferRecommendedComment offerRecommendedComment, BindingResult errors,
            ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(application, user, CONFIRM_OFFER_RECOMMENDATION);

        if (errors.hasErrors()) {
            modelMap.put("offerRecommendedComment", offerRecommendedComment);
            return OFFER_RECOMMENDATION_VIEW_NAME;
        }
        
        if (offerRecommendedService.moveToApproved(application, offerRecommendedComment)) {
            offerRecommendedService.sendToPortico(application);
            modelMap.put("messageCode", "move.approved");
            modelMap.put("application", application.getApplicationNumber());
            applicationFormUserRoleService.registerApplicationUpdate(application, ApplicationUpdateScope.ALL_USERS);
            return "redirect:/applications";
        } else {
            return "redirect:/rejectApplication?applicationId=" + application.getApplicationNumber() + "&rejectionId=7";
        }

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

    protected RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("offerRecommendedComment")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(offerRecommendedCommentValidator);
        binder.registerCustomEditor(Supervisor.class, supervisorPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }

    @ModelAttribute("nominatedSupervisors")
    public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
        return supervisorsProvider.getNominatedSupervisors(applicationId);
    }

    @ModelAttribute("previousSupervisors")
    public List<RegisteredUser> getPreviousSupervisorsAndInterviewersWillingToSupervise(@RequestParam String applicationId) {
        return supervisorsProvider.getPreviousSupervisorsAndInterviewersWillingToSupervise(applicationId);
    }
}
