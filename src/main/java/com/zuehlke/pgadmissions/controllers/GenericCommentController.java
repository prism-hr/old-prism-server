package com.zuehlke.pgadmissions.controllers;

import java.util.List;

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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
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

	public GenericCommentController() {
		this(null, null, null, null, null);
	}

    @Autowired
    public GenericCommentController(ApplicationsService applicationsService,
            UserService userService, CommentService commentService,
            GenericCommentValidator genericCommentValidator,
            DocumentPropertyEditor documentPropertyEditor) {
	this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.genericCommentValidator = genericCommentValidator;
		this.documentPropertyEditor = documentPropertyEditor;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
		
		if (applicationForm == null) {
		    throw new ResourceNotFoundException();
		}
		
		if (currentUser.isInRole(Authority.APPLICANT) || currentUser.isRefereeOfApplicationForm(applicationForm) || !currentUser.canSee(applicationForm)) {
			// overwrite the decision if the currentUser is in fact the ADMINISTRATOR or SUPERADMINISTRATOR
		    if (!(listContainsId(currentUser, applicationForm.getProgram().getAdministrators()) || currentUser.isInRole(Authority.SUPERADMINISTRATOR))) {
		        throw new ResourceNotFoundException();
		    }
		}
		
		return applicationForm;
	}
	
    @ModelAttribute("actionsDefinition")
    public ActionsDefinitions getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.calculateActions(getUser(), application);
    }
	
	@RequestMapping(method = RequestMethod.GET)
	public String getGenericCommentPage() {
		return GENERIC_COMMENT_PAGE;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("comment")
	public Comment getComment(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setUser(userService.getCurrentUser());
		return comment;
	}

	@InitBinder(value = "comment")
	public void registerBinders(WebDataBinder binder) {
		binder.setValidator(genericCommentValidator);
		binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
		binder.registerCustomEditor(Document.class, documentPropertyEditor);

	}

	@RequestMapping(method = RequestMethod.POST)
	public String addComment(@Valid @ModelAttribute("comment") Comment comment, BindingResult result) {
		if (result.hasErrors()) {
			return GENERIC_COMMENT_PAGE;
		}
		commentService.save(comment);
		return "redirect:/comment?applicationId=" + comment.getApplication().getApplicationNumber();
	}
	
    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }   	
}
