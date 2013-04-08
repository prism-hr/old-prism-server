package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping(value = { "/interviewFeedback" })
public class InterviewCommentController {

    private static final String INTERVIEW_FEEDBACK_PAGE = "private/staff/interviewers/feedback/interview_feedback";
    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final FeedbackCommentValidator feedbackCommentValidator;
    private final CommentService commentService;
    private final DocumentPropertyEditor documentPropertyEditor;

    public InterviewCommentController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public InterviewCommentController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            FeedbackCommentValidator reviewFeedbackValidator, DocumentPropertyEditor documentPropertyEditor) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.commentService = commentService;
        this.feedbackCommentValidator = reviewFeedbackValidator;
        this.documentPropertyEditor = documentPropertyEditor;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!currentUser.isInterviewerOfApplicationForm(applicationForm) || !currentUser.canSee(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if (applicationForm.isDecided() || applicationForm.isWithdrawn() || currentUser.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationForm)) {
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        return applicationForm;
    }
    
    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getInterviewFeedbackPage() {
        return INTERVIEW_FEEDBACK_PAGE;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("comment")
    public InterviewComment getComment(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser currentUser = getUser();

        InterviewComment interviewComment = new InterviewComment();
        interviewComment.setApplication(applicationForm);
        interviewComment.setUser(currentUser);
        interviewComment.setComment("");
        interviewComment.setType(CommentType.INTERVIEW);
        interviewComment.setInterviewer(currentUser.getInterviewersForApplicationForm(applicationForm).get(0));
        return interviewComment;
    }

    @InitBinder(value = "comment")
    public void registerBinders(WebDataBinder binder) {
        binder.setValidator(feedbackCommentValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComment(@Valid @ModelAttribute("comment") InterviewComment comment, BindingResult result) {
        if (result.hasErrors()) {
            return INTERVIEW_FEEDBACK_PAGE;
        }
        commentService.save(comment);
        return "redirect:/applications?messageCode=interview.feedback&application=" + comment.getApplication().getApplicationNumber();
    }
}
