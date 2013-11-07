package com.zuehlke.pgadmissions.controllers.workflow;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_ELIGIBILITY;

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
import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdmitterCommentValidator;

@Controller
@RequestMapping("/admitter")
public class AdmitterCommentController {

    private static final String GENERIC_COMMENT_PAGE = "private/staff/admin/comment/genericcomment";

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdmitterCommentValidator admitterCommentValidator;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private ActionsProvider actionsProvider;

    @Autowired
    private ApplicationFormAccessService accessService;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private EventFactory eventFactory;

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

    @ModelAttribute("comment")
    public AdmitterComment getComment(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        AdmitterComment comment = new AdmitterComment();
        comment.setApplication(applicationForm);
        comment.setUser(userService.getCurrentUser());
        return comment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(admitterCommentValidator);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @ModelAttribute("isConfirmEligibilityComment")
    public Boolean isConfirmElegibilityComment() {
        return true;
    }

    @RequestMapping(value = "/confirmEligibility", method = RequestMethod.GET)
    public String getConfirmEligibilityPage(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, CONFIRM_ELIGIBILITY);
        accessService.deregisterApplicationUpdate(applicationForm, user);
        return GENERIC_COMMENT_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/confirmEligibility")
    public String confirmEligibility(ModelMap modelMap, @Valid @ModelAttribute("comment") AdmitterComment comment, BindingResult result) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(application, user, CONFIRM_ELIGIBILITY);

        if (result.hasErrors()) {
            return GENERIC_COMMENT_PAGE;
        }

        comment.setUser(user);
        comment.setDate(new Date());
        comment.setApplication(application);
        
        // These do not do anything any more. we can deprecate them.
        application.setAdminRequestedRegistry(null);
        application.setRegistryUsersDueNotification(false);
        application.addApplicationUpdate(new ApplicationFormUpdate(application, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(application, user, new Date());
        
        application.getEvents().add(eventFactory.createEvent(comment));
        commentService.save(comment);
        applicationsService.save(application);
        accessService.admitterCommentPosted(comment);
        accessService.registerApplicationUpdate(application, new Date(), ApplicationUpdateScope.INTERNAL);
        return "redirect:/applications?messageCode=validation.comment.success&application=" + application.getApplicationNumber();
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
