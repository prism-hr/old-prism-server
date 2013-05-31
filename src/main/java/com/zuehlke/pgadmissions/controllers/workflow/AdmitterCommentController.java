package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.mail.MailSendingService;
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
public class AdmitterCommentController extends StateTransitionController {

    private final MailSendingService mailService;

    public AdmitterCommentController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public AdmitterCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, StateTransitionService stateTransitionService,
            ApplicationFormAccessService accessService, final MailSendingService mailService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, accessService, actionsProvider);
        this.mailService = mailService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitRegistryValidationComment")
    public String defaultGet() {
        return "redirect:/applications";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitRegistryValidationComment")
    public String submitAdmitterComment(@RequestParam String applicationId, @Valid @ModelAttribute("comment") ValidationComment comment, BindingResult result) {

        if (result.hasErrors()) {
            return STATE_TRANSITION_VIEW;
        }

        ApplicationForm form = getApplicationForm(applicationId);
        comment.setUser(getCurrentUser());
        comment.setDate(new Date());
        comment.setApplication(form);
        comment.setNextStatus(form.getStatus());
        form.setAdminRequestedRegistry(null);
        form.setRegistryUsersDueNotification(false);

        form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(form, getCurrentUser(), new Date());
        applicationsService.save(form);
        commentService.save(comment);

        mailService.scheduleAdmitterProvidedCommentNotification(form);

        return "redirect:/applications?messageCode=validation.comment.success&application=" + form.getApplicationNumber();
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
