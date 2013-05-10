package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
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

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.propertyeditors.MoveToReviewReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;

@Controller
@RequestMapping("/review")
public class MoveToReviewController extends ReviewController {

	private final ReviewRoundValidator reviewRoundValidator;
	private final MoveToReviewReviewerPropertyEditor reviewerPropertyEditor;

	MoveToReviewController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public MoveToReviewController(ApplicationsService applicationsService, UserService userService, ReviewService reviewService,
	                ReviewRoundValidator reviewRoundValidator, MoveToReviewReviewerPropertyEditor reviewerPropertyEditor) {
		super(applicationsService, userService, reviewService);
		this.reviewRoundValidator = reviewRoundValidator;
		this.reviewerPropertyEditor = reviewerPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToReview")
	public String getReviewRoundDetailsPage(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return REVIEW_DETAILS_VIEW_NAME;
	}

	@RequestMapping(method = RequestMethod.GET, value = "reviewersSection")
	public String getReviewersSectionView(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return REVIEWERS_SECTION_NAME;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToReview(@RequestParam String applicationId, @Valid @ModelAttribute("reviewRound") ReviewRound reviewRound, BindingResult bindingResult) {

		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return REVIEWERS_SECTION_NAME;
		}
		reviewService.moveApplicationToReview(applicationForm, reviewRound);
		return "/private/common/ajax_OK";
	}

	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam Object applicationId) {
		ReviewRound reviewRound = new ReviewRound();
		ApplicationForm applicationForm = getApplicationForm((String) applicationId);
		ReviewRound latestReviewRound = applicationForm.getLatestReviewRound();
		if (latestReviewRound != null) {
			List<Reviewer> newReviewers = Lists.newArrayList();
			for (Reviewer lastReviewer : latestReviewRound.getReviewers()) {
				ReviewComment lastReview = lastReviewer.getReview();
				if (lastReview == null || !BooleanUtils.isTrue(lastReview.isDecline())) {
					newReviewers.add(lastReviewer);
				}
			}
			reviewRound.setReviewers(newReviewers);
		}
		return reviewRound;
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
