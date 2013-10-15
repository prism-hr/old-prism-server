package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION;

import java.util.Date;

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
import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OfferRecommendedCommentValidator;

@Controller
@RequestMapping(value = { "/offerRecommendation" })
public class OfferRecommendationController {

    private static final String OFFER_RECOMMENDATION_VIEW_NAME = "private/staff/approver/offer_recommendation_page";

    private final UserService userService;

    private final ApplicationsService applicationsService;

    private final ApplicationFormAccessService accessService;

    private final ActionsProvider actionsProvider;

    private final ApplicationDescriptorProvider applicationDescriptorProvider;

    private final ApprovalService approvalService;

    private final OfferRecommendedCommentValidator offerRecommendedCommentValidator;

    private final DatePropertyEditor datePropertyEditor;
    
    private final ProgramInstanceService programInstanceService;

    public OfferRecommendationController() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public OfferRecommendationController(ApplicationsService applicationsService, UserService userService, ActionsProvider actionsProvider,
            ApplicationFormAccessService accessService, ApplicationDescriptorProvider applicationDescriptorProvider, ApprovalService approvalService,
            OfferRecommendedCommentValidator offerRecommendedCommentValidator, DatePropertyEditor datePropertyEditor,
            ProgramInstanceService programInstanceService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
        this.applicationDescriptorProvider = applicationDescriptorProvider;
        this.approvalService = approvalService;
        this.offerRecommendedCommentValidator = offerRecommendedCommentValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programInstanceService = programInstanceService;
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
            
            if (startDate.before(programInstanceService.getEarliestPossibleStartDate(application))) {
            	startDate = programInstanceService.getEarliestPossibleStartDate(application);
            }
            
            offerRecommendedComment.setRecommendedStartDate(startDate);
            offerRecommendedComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
            offerRecommendedComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
            offerRecommendedComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
        }
        modelMap.put("offerRecommendedComment", offerRecommendedComment);

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
        application.addApplicationUpdate(new ApplicationFormUpdate(application, ApplicationUpdateScope.ALL_USERS, new Date()));
        accessService.updateAccessTimestamp(application, getCurrentUser(), new Date());

        if (approvalService.moveToApproved(application, offerRecommendedComment)) {
            approvalService.sendToPortico(application);
            modelMap.put("messageCode", "move.approved");
            modelMap.put("application", application.getApplicationNumber());
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
        return applicationDescriptorProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    protected RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("offerRecommendedComment")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(offerRecommendedCommentValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}
