package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientPrivilegesException;
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
public class AdmitterCommentController  {
    
    private static final String GENERIC_COMMENT_PAGE = "private/staff/admin/comment/genericcomment";
    
    @Autowired
    private  ApplicationsService applicationsService;
    
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
        RegisteredUser currentUser = getUser();
        if (currentUser.isNotInRole(Authority.ADMITTER) && currentUser.isNotInRole(Authority.SUPERADMINISTRATOR)) {
            throw new InsufficientPrivilegesException();
        }
        
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new ResourceNotFoundException();
        }
        
        
        return applicationForm;
    }
    
    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return actionsProvider.calculateActions(getUser(), application);
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

    @RequestMapping(value ="/confirmEligibility", method = RequestMethod.GET)
    public String getGenericCommentPage() {
        return GENERIC_COMMENT_PAGE;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitAdmitterComment")
    public String defaultGet() {
        return "redirect:/applications";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/submitAdmitterComment")
    public String submitAdmitterComment(@RequestParam String applicationId, @Valid @ModelAttribute("comment") AdmitterComment comment, BindingResult result) {
        if (getUser().isNotInRole(Authority.ADMITTER) && getUser().isNotInRole(Authority.SUPERADMINISTRATOR)) {
            throw new InsufficientPrivilegesException();
        }

        if (result.hasErrors()) {
            return GENERIC_COMMENT_PAGE;
        }

        ApplicationForm form = getApplicationForm(applicationId);
        comment.setUser(getUser());
        comment.setDate(new Date());
        comment.setApplication(form);
        commentService.save(comment);
        form.setAdminRequestedRegistry(null);
        form.setRegistryUsersDueNotification(false);
        form.getEvents().add(eventFactory.createEvent(comment));
        form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(form, getUser(), new Date());
        applicationsService.save(form);

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
