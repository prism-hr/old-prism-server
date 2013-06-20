package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class ValidationTransitionController extends StateTransitionController {

    public ValidationTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ValidationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, accessService, actionsProvider);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPage")
    public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm) {
        return stateTransitionService.resolveView(applicationForm);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitValidationComment")
    public String defaultGet() {
        return "redirect:/applications";
    }

    @ModelAttribute("comment")
    public ValidationComment getComment(@RequestParam String applicationId) {
        ValidationComment validationComment = new ValidationComment();
        validationComment.setApplication(getApplicationForm(applicationId));
        validationComment.setUser(getCurrentUser());
        return validationComment;
    }

    @RequestMapping(value = "/submitValidationComment", method = RequestMethod.POST)
    public String addComment(@RequestParam String applicationId, @Valid @ModelAttribute("comment") ValidationComment comment, BindingResult result,
            ModelMap model, @RequestParam(required = false) Boolean delegate, @ModelAttribute("delegatedInterviewer") RegisteredUser delegatedInterviewer,
            @RequestParam(required = false) Boolean fastTrackApplication) {

        model.put("delegate", delegate);
        ApplicationForm form = getApplicationForm(applicationId);
        RegisteredUser user = getCurrentUser();
        
        // validate action is still available
        actionsProvider.validateAction(form, user, ApplicationFormAction.COMPLETE_VALIDATION_STAGE);
        
        try {
            if ((fastTrackApplication == null && form.getBatchDeadline() != null) || result.hasErrors()) {
                if (fastTrackApplication == null) {
                    model.addAttribute("fastTrackMissing", true);
                }
                return STATE_TRANSITION_VIEW;
            }

            if (BooleanUtils.isNotTrue(delegate)) {
                form.setApplicationAdministrator(null);
            }

            if (BooleanUtils.isTrue(fastTrackApplication)) {
                applicationsService.fastTrackApplication(form.getApplicationNumber());
            }

            form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.INTERNAL, new Date()));
            accessService.updateAccessTimestamp(form, user, new Date());
            applicationsService.save(form);
            comment.setDate(new Date());
            commentService.save(comment);

            if (comment.getNextStatus() == ApplicationFormStatus.APPROVAL) {
                applicationsService.makeApplicationNotEditable(form);
            }

            if (answeredOneOfTheQuestionsUnsure(comment) && comment.getNextStatus() != ApplicationFormStatus.REJECTED) {
                form.setAdminRequestedRegistry(user);
                form.setRegistryUsersDueNotification(true);
                applicationsService.save(form);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(delegate)) {
            return "redirect:/applications?messageCode=delegate.success&application=" + form.getApplicationNumber();
        }

        applicationsService.refresh(form);
        return stateTransitionService.resolveView(form);
    }

    private boolean answeredOneOfTheQuestionsUnsure(final ValidationComment comment) {
        return comment.getHomeOrOverseas() == HomeOrOverseas.UNSURE || comment.getQualifiedForPhd() == ValidationQuestionOptions.UNSURE
                || comment.getEnglishCompentencyOk() == ValidationQuestionOptions.UNSURE;
    }

    @ModelAttribute("validationQuestionOptions")
    public ValidationQuestionOptions[] getValidationQuestionOptions() {
        return ValidationQuestionOptions.values();
    }

    @ModelAttribute("homeOrOverseasOptions")
    public HomeOrOverseas[] getHomeOrOverseasOptions() {
        return HomeOrOverseas.values();
    }

}
