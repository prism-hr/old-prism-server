package com.zuehlke.pgadmissions.controllers.referees;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;
import com.zuehlke.pgadmissions.validators.ReferenceValidator;

@Controller
@RequestMapping("/referee")
public class ReferenceController {
	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	private static final String EXPIRED_VIEW_NAME = "private/referees/upload_references_expired";
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
		if (applicationForm == null || !getCurrentUser().isRefereeOfApplicationForm(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {				
		return getCurrentUser();
	}

	@RequestMapping(value = "/addReferences", method = RequestMethod.GET)
	public String getUploadReferencesPage(@ModelAttribute ApplicationForm applicationForm) {
		if (applicationForm.isDecided()) {
			return EXPIRED_VIEW_NAME;
		}
		return ADD_REFERENCES_VIEW_NAME;
	}

	@ModelAttribute("comment")
	public ReferenceComment getComment(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		RegisteredUser currentUser = getCurrentUser();
		Referee refereeForApplicationForm = currentUser.getRefereeForApplicationForm(applicationForm);
		Referee referee = refereeForApplicationForm;
		if (referee == null || referee.getReference() == null) {
			ReferenceComment referenceComment = new ReferenceComment();
			referenceComment.setApplication(applicationForm);
			referenceComment.setUser(currentUser);
			referenceComment.setComment("");
			referenceComment.setType(CommentType.REFERENCE);
			referenceComment.setReferee(refereeForApplicationForm);
			return referenceComment;
		}
		return referee.getReference();
	}
	
	@InitBinder("comment")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(referenceValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
	}
	
	@RequestMapping(value = "/submitReference", method = RequestMethod.POST)
	public String handleReferenceSubmission(@Valid @ModelAttribute("comment") ReferenceComment comment, BindingResult bindingResult) {
		if(bindingResult.hasErrors()){
			return ADD_REFERENCES_VIEW_NAME;
		}
		commentService.save(comment);		
		refereeService.saveReferenceAndSendMailNotifications(comment.getReferee());
		return "redirect:/addReferences/referenceuploaded";
	}

}
