package com.zuehlke.pgadmissions.controllers;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.security.ActionsProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;

@Controller
@RequestMapping(value = { "/comment" })
public class GenericCommentController {

    private static final String GENERIC_COMMENT_PAGE = "private/staff/admin/comment/genericcomment";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final GenericCommentValidator genericCommentValidator;

    private final CommentService commentService;

    private final DocumentPropertyEditor documentPropertyEditor;

    private final ActionsProvider actionsProvider;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public GenericCommentController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public GenericCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            GenericCommentValidator genericCommentValidator, DocumentPropertyEditor documentPropertyEditor, ActionsProvider actionsProvider,
            ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.genericCommentValidator = genericCommentValidator;
        this.documentPropertyEditor = documentPropertyEditor;
        this.actionsProvider = actionsProvider;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        actionsProvider.validateAction(applicationForm, getCurrentUser(), ApplicationFormAction.COMMENT);
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        return actionsProvider.getApplicationDescriptorForUser(getApplicationForm(applicationId), getCurrentUser());
    }

    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public Comment getComment(@RequestParam String applicationId) {
        Comment comment = new Comment();
        comment.setApplication(getApplicationForm(applicationId));
        comment.setUser(getCurrentUser());
        return comment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(genericCommentValidator);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(Document.class, documentPropertyEditor);

    }

    @RequestMapping(method = RequestMethod.GET)
    public String getGenericCommentPage(ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        applicationFormUserRoleService.applicationViewed(applicationForm, getCurrentUser());
        return GENERIC_COMMENT_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@Valid @ModelAttribute("comment") Comment comment, BindingResult result, ModelMap modelMap, @ModelAttribute ApplicationForm applicationForm) {
        if (result.hasErrors()) {
            return GENERIC_COMMENT_PAGE;
        }
        
        commentService.save(comment);
        applicationsService.save(applicationForm);
        applicationFormUserRoleService.commentPosted(applicationForm, getCurrentUser());
        return "redirect:/comment?applicationId=" + applicationForm.getApplicationNumber();
    }

}