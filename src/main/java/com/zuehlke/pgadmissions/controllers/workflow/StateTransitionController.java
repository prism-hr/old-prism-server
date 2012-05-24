package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;

@Controller
@RequestMapping("/progress")
public class StateTransitionController {

	private static final String STATE_TRANSITION_VIEW = "private/staff/admin/state_transition";
	private static final String SIMPLE_MESSAGE_VIEW ="private/common/simpleMessage";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final CommentService commentService;
	private final CommentFactory commentFactory;

	StateTransitionController() {
		this(null, null, null, null);

	}

	@Autowired
	public StateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			CommentFactory commentFactory) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.commentFactory = commentFactory;

	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer application) {
		ApplicationForm applicationForm = applicationsService.getApplicationById(application);
		if (applicationForm == null || !getCurrentUser().isInRoleInProgram(Authority.ADMINISTRATOR, applicationForm.getProgram())) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	RegisteredUser getCurrentUser() {
		
		return userService.getCurrentUser();
	}

	@ModelAttribute("stati")
	public ApplicationFormStatus[] getAvailableNextStati(@RequestParam Integer application) {
		ApplicationForm applicationForm = getApplicationForm(application);
		return ApplicationFormStatus.getAvailableNextStati(applicationForm.getStatus());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getStateTransitionView() {
		return STATE_TRANSITION_VIEW;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String addComment(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("user") RegisteredUser user,
			@RequestParam CommentType type, @RequestParam String comment) {
		if (StringUtils.isNotBlank(comment)) {
			Comment newComment = commentFactory.createComment(applicationForm, user, comment, type);
			commentService.save(newComment);
		}
		return SIMPLE_MESSAGE_VIEW;
	}

	@ModelAttribute("reviewersWillingToInterview")
	public List<RegisteredUser> getReviewersWillingToInterview( @RequestParam Integer application) {
		ApplicationForm applicationForm = getApplicationForm(application);
		if(applicationForm.getStatus() == ApplicationFormStatus.REVIEW){
			return userService.getReviewersWillingToInterview(applicationForm);
		}
		return null;
	}

}
