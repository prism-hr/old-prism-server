package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotCommentException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationReviewService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
public class CommentController {

	private static final String COMMENTS_VIEW = "private/staff/admin/timeline";
	private final ApplicationReviewService applicationReviewService;
	private final ApplicationsService applicationService;
	private final UserService userService;

	CommentController() {
		this(null, null, null);
	}

	@Autowired
	public CommentController(ApplicationReviewService applicationReviewService,
			ApplicationsService applicationService, UserService userService) {
		this.applicationReviewService = applicationReviewService;
		this.applicationService = applicationService;
		this.userService = userService;

	}
	
	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	public ModelAndView getCommentedApplicationPage(@RequestParam Integer id, @RequestParam String comment) {
		ApplicationReview applicationReview = new ApplicationReview();
		ApplicationForm application = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT) || !application.isModifiable())
		{
			throw new CannotCommentException();
		}
		else 
		{
			applicationReview.setApplication(application);
			applicationReview.setComment(comment);
			applicationReview.setUser(user);
			applicationReviewService.save(applicationReview);
		}
		return new  ModelAndView("redirect:/application?view=comments","applicationId", application.getId());
	}

	@RequestMapping(value = { "/showAll" }, method = RequestMethod.GET)
	@Deprecated
	public ModelAndView getAllCommentsForApplication(@RequestParam Integer id) {
		ApplicationForm application = applicationService.getApplicationById(id);
		return new  ModelAndView("redirect:/application?view=comments","applicationId", application.getId());
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer id) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm applicationForm = applicationService.getApplicationById(id);
		if(applicationForm == null || currentUser.isInRole(Authority.APPLICANT) || !currentUser.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;	
	}

	@ModelAttribute("comments")
	public List<ApplicationReview> getComments(@RequestParam Integer id) {
		return getApplicationForm(id).getVisibleComments(userService.getCurrentUser());
	}
	
	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String getCommentsView() {		
		return COMMENTS_VIEW;
	}

	

}
