package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Date;

import javax.validation.Valid;

import org.joda.time.DateTime;
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

import com.zuehlke.pgadmissions.domain.AdmitterComment;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.AdmitterCommentValidator;

@Controller
@RequestMapping("/admitter")
public class AdmitterCommentController {

    private static final String GENERIC_COMMENT_PAGE = "private/staff/admin/comment/genericcomment";

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdmitterCommentValidator admitterCommentValidator;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private ActionService actionService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private MailSendingService mailService;

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
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("comment")
    public AdmitterComment getComment(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
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
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_CONFIRM_ELIGIBILITY);
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return GENERIC_COMMENT_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/confirmEligibility")
    public String confirmEligibility(ModelMap modelMap, @Valid @ModelAttribute("comment") AdmitterComment comment, BindingResult result) {
        Application application = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(application, user, SystemAction.APPLICATION_CONFIRM_ELIGIBILITY);

        if (result.hasErrors()) {
            return GENERIC_COMMENT_PAGE;
        }

        comment.setUser(user);
        comment.setCreatedTimestamp(new DateTime());
        comment.setApplication(application);
        
        commentService.save(comment);
        applicationsService.saveUpdate(application);
        applicationFormUserRoleService.admitterCommentPosted(comment);
        applicationFormUserRoleService.applicationUpdated(application, user);
        return "redirect:/applications?messageCode=validation.comment.success&application=" + application.getApplicationNumber();
    }

    @ModelAttribute("validationQuestionOptions")
    public ValidationQuestionOptions[] getValidationQuestionOptions() {
        return ValidationQuestionOptions.values();
    }

    @ModelAttribute("homeOrOverseasOptions")
    public ResidenceStatus[] getHomeOrOverseasOptions() {
        return ResidenceStatus.values();
    }

}