package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationReviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Controller
@RequestMapping(value={"/application"})
public class AssignReviewerController {

	private static final String REVIEW_SUCCESS_VIEW_NAME = "reviewSuccess";
	private static final String REVIEW_APPLICATION_VIEW_NAME = "reviewApplication";
	private final ApplicationFormDAO applicationFormDAO;
	private final ApplicationReviewDAO applicationReviewDAO;
	
	AssignReviewerController(){
		this(null, null);
	}

	@Autowired
	public AssignReviewerController(ApplicationFormDAO applicationFormDAO,
									ApplicationReviewDAO applicationReviewDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.applicationReviewDAO = applicationReviewDAO;
	}


	@RequestMapping(value={"/review"},method = RequestMethod.GET)
	@Transactional
	public String assignReviewer(HttpServletRequest request, ModelMap modelMap) {
		SecurityContext context = SecurityContextHolder.getContext();
		String id = request.getParameter("id");
		ApplicationForm applicationUnderReview = applicationFormDAO.get(Integer.parseInt(id));
		applicationUnderReview.setReviewer((RegisteredUser)context.getAuthentication().getDetails());
		
		applicationFormDAO.save(applicationUnderReview);
		modelMap.addAttribute("application", applicationUnderReview);
		
		return REVIEW_APPLICATION_VIEW_NAME;
	}
	
	@RequestMapping(value={"/submit"},method = RequestMethod.GET)
	@Transactional
	public String getSubmittedReviewPage(HttpServletRequest request,
			ModelMap modelMap) {
		SecurityContext context = SecurityContextHolder.getContext();
		ApplicationForm application = (ApplicationForm) modelMap.get("application");
		
		ApplicationReview review = new ApplicationReview();
		review.setUser((RegisteredUser)context.getAuthentication().getDetails());
		review.setApplication(application);
		review.setComment(request.getParameter("comment"));
		
		applicationReviewDAO.save(review);
		modelMap.addAttribute("review", review);
		
		return REVIEW_SUCCESS_VIEW_NAME;
	}
	
	
}
