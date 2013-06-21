package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.List;

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

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.propertyeditors.AssignReviewersReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

@Controller
@RequestMapping("/review")
public class AssignReviewerController extends ReviewController {

	private final ReviewRoundValidator reviewRoundValidator;
	private final AssignReviewersReviewerPropertyEditor reviewerPropertyEditor;


	AssignReviewerController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public AssignReviewerController(ApplicationsService applicationsService, UserService userService, ReviewService reviewService, 
			ReviewRoundValidator reviewRoundValidator,  AssignReviewersReviewerPropertyEditor reviewerPropertyEditor, ActionsProvider actionsProvider) {
		super(applicationsService, userService, reviewService, actionsProvider);
		this.reviewRoundValidator = reviewRoundValidator;
		this.reviewerPropertyEditor = reviewerPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "assignReviewers")
	public String getAssignReviewersPage(ModelMap modelMap) {
		modelMap.put("assignOnly", true);
		return REVIEW_DETAILS_VIEW_NAME;
	}

	@RequestMapping(method = RequestMethod.GET, value = "assignReviewersSection")
	public String getReviewersSectionView(ModelMap modelMap) {
		modelMap.put("assignOnly", true);
		return REVIEWERS_SECTION_NAME;
	}
	
	@Override
	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam Object applicationId) {
		return getApplicationForm((String) applicationId).getLatestReviewRound();

	}

	@RequestMapping(value = "/assign", method = RequestMethod.POST)
	public String assignReviewers(@Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return REVIEWERS_SECTION_NAME;
		}
		
		List<Reviewer> reviewers = reviewRound.getReviewers();
		for (Reviewer reviewer : reviewers) {
			if (reviewer.getId() == null) {
				reviewer.setRequiresAdminNotification(CheckedStatus.YES);
			}
		}
		reviewService.save(reviewRound);
		return"/private/common/ajax_OK";
	}
	

	@InitBinder(value = "reviewRound")
	public void registerReviewRoundValidator(WebDataBinder binder) {
		binder.setValidator(reviewRoundValidator);
		binder.registerCustomEditor(Reviewer.class, reviewerPropertyEditor);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
	}
	        
	public StringTrimmerEditor newStringTrimmerEditor() {
	    return new StringTrimmerEditor(false);
	}
}
