package com.zuehlke.pgadmissions.controllers.referees;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping("/referee")
public class ReferenceController {

    private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
    private final ApplicationsService applicationsService;
    private final DocumentPropertyEditor documentPropertyEditor;
    private final FeedbackCommentValidator referenceValidator;
    private final RefereeService refereeService;
    private final UserService userService;
    private final CommentService commentService;

    ReferenceController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ReferenceController(ApplicationsService applicationsService, RefereeService refereeService, UserService userService,
            DocumentPropertyEditor documentPropertyEditor, FeedbackCommentValidator referenceValidator, CommentService commentService) {
        this.applicationsService = applicationsService;
        this.refereeService = refereeService;
        this.userService = userService;
        this.documentPropertyEditor = documentPropertyEditor;
        this.referenceValidator = referenceValidator;
        this.commentService = commentService;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        RegisteredUser currentUser = getCurrentUser();

        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.isRefereeOfApplicationForm(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if (applicationForm.isDecided() || applicationForm.isWithdrawn() || currentUser.getRefereeForApplicationForm(applicationForm).hasResponded()) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }
    
    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }

    @RequestMapping(value = "/addReferences", method = RequestMethod.GET)
    public String getUploadReferencesPage() {
        return ADD_REFERENCES_VIEW_NAME;
    }

    @ModelAttribute("comment")
    public ReferenceComment getComment(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser currentUser = getCurrentUser();
        Referee refereeForApplicationForm = currentUser.getRefereeForApplicationForm(applicationForm);

        ReferenceComment referenceComment = new ReferenceComment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setUser(currentUser);
        referenceComment.setComment("");
        referenceComment.setType(CommentType.REFERENCE);
        referenceComment.setReferee(refereeForApplicationForm);
        return referenceComment;
    }

    @InitBinder("comment")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(referenceValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @RequestMapping(value = "/submitReference", method = RequestMethod.POST)
    public String handleReferenceSubmission(@Valid @ModelAttribute("comment") ReferenceComment comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ADD_REFERENCES_VIEW_NAME;
        }

        if (comment.getReferee().getReference() == null) { // check if the reference isn't already submitted
            commentService.save(comment);
            refereeService.saveReferenceAndSendMailNotifications(comment.getReferee());
        }
        return "redirect:/applications?messageCode=reference.uploaded&application=" + comment.getApplication().getApplicationNumber();
    }

}
