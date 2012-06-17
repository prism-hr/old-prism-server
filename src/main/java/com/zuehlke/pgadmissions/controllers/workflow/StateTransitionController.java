package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;

@Controller
@RequestMapping("/progress")
public class StateTransitionController {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final CommentService commentService;
	private final CommentFactory commentFactory;
	private final StateTransitionViewResolver stateTransitionViewResolver;

	StateTransitionController() {
		this(null, null, null, null, null);

	}

	@Autowired
	public StateTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			CommentFactory commentFactory, StateTransitionViewResolver stateTransitionViewResolver) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.commentService = commentService;
		this.commentFactory = commentFactory;
		this.stateTransitionViewResolver = stateTransitionViewResolver;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String application) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(application);
		if (applicationForm == null || !getCurrentUser().hasAdminRightsOnApplication(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;

	}

	RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("stati")
	public ApplicationFormStatus[] getAvailableNextStati(@RequestParam String application) {
		ApplicationForm applicationForm = getApplicationForm(application);
		return ApplicationFormStatus.getAvailableNextStati(applicationForm.getStatus());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm) {
		return stateTransitionViewResolver.resolveView(applicationForm);
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String addComment(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("user") RegisteredUser user,
			@RequestParam CommentType type, @RequestParam String comment,@RequestParam ApplicationFormStatus nextStatus,  @RequestParam(required = false) ValidationQuestionOptions qualifiedForPhd,
			@RequestParam(required = false) ValidationQuestionOptions englishCompentencyOk, @RequestParam(required = false) HomeOrOverseas homeOrOverseas) {
		if (StringUtils.isNotBlank(comment)) {
			Comment newComment = commentFactory.createComment(applicationForm, user, comment, type,nextStatus);
			if(newComment instanceof ValidationComment){
				((ValidationComment) newComment).setEnglishCompentencyOk(englishCompentencyOk);
				((ValidationComment) newComment).setQualifiedForPhd(qualifiedForPhd);
				((ValidationComment) newComment).setHomeOrOverseas(homeOrOverseas);
				
			}
			if(newComment instanceof ReviewEvaluationComment){
				((ReviewEvaluationComment) newComment).setReviewRound(applicationForm.getLatestReviewRound());
			}
			if(newComment instanceof InterviewEvaluationComment){
				((InterviewEvaluationComment) newComment).setInterview(applicationForm.getLatestInterview());
			}
			commentService.save(newComment);
		}
		return stateTransitionViewResolver.resolveView(applicationForm);
	}

	@ModelAttribute("reviewersWillingToInterview")
	public List<RegisteredUser> getReviewersWillingToInterview(@RequestParam String application) {
		ApplicationForm applicationForm = getApplicationForm(application);
		if (applicationForm.getStatus() == ApplicationFormStatus.REVIEW) {
			return userService.getReviewersWillingToInterview(applicationForm);
		}
		return null;
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
