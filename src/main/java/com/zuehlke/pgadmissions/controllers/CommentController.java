package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotCommentException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/comments" })
@Deprecated
public class CommentController {


	private final CommentService commentService;
	private final ApplicationsService applicationService;
	private final UserService userService;

	CommentController() {
		this(null, null, null);
	}

	@Autowired
	public CommentController(CommentService commentService,
			ApplicationsService applicationService, UserService userService) {
		this.commentService = commentService;
		this.applicationService = applicationService;
		this.userService = userService;

	}
	
	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	@Deprecated
	public ModelAndView getCommentedApplicationPage(@RequestParam Integer id, @RequestParam String comment) {
		Comment cmt = new Comment();
		ApplicationForm application = applicationService.getApplicationById(id);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(user.isInRole(Authority.APPLICANT) || !application.isModifiable())
		{
			throw new CannotCommentException();
		}
		else 
		{
			cmt.setApplication(application);
			cmt.setComment(comment);
			cmt.setUser(user);
			commentService.save(cmt);
		}
		return new  ModelAndView("redirect:/application?view=comments","applicationId", application.getId());
	}

	@RequestMapping(value = { "/showAll" }, method = RequestMethod.GET)
	@Deprecated
	public ModelAndView getAllCommentsForApplication(@RequestParam Integer id) {
		ApplicationForm application = applicationService.getApplicationById(id);
		return new  ModelAndView("redirect:/application?view=comments","applicationId", application.getId());
	}


	public String getGenericCommentsView() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
