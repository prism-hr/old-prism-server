package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/review")
public class MoveToReviewController extends AssignReviewerController {

	MoveToReviewController() {
		this(null, null, null, null, null);		
	}

	@Autowired
	public MoveToReviewController(ApplicationsService applicationServiceMock, ReviewService reviewService, UserService userService,
			NewUserByAdminValidator validator, MessageSource msgSource) {
		super(applicationServiceMock, reviewService, userService, validator, msgSource);	
	}

	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(@RequestParam(required = false) Integer applicationId) {
		return new ReviewRound();
	}

	@RequestMapping(method = RequestMethod.GET, value="moveToReview")
	public String getMoveToReviewPage(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}
	
	@RequestMapping(value = "/moveApplicationToReview", method = RequestMethod.POST)
	public String moveApplicationToReviewState(@ModelAttribute("applicationForm") ApplicationForm application, @ModelAttribute("reviewRound") ReviewRound reviewRound,	@ModelAttribute("unsavedReviewers") ArrayList<RegisteredUser> unsavedReviewers) {

		checkApplicationStatus(application);
		checkAdminPermission(application.getProgram());

		if (unsavedReviewers != null && !unsavedReviewers.isEmpty()) {
			reviewService.moveApplicationToReview(application, reviewRound, unsavedReviewers.toArray(new RegisteredUser[unsavedReviewers.size()]));
		}
		return NEXT_VIEW;
	}

	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public String createReviewer(@ModelAttribute("programme") Program programme, @ModelAttribute("applicationForm") ApplicationForm form,
			@Valid @ModelAttribute("uiReviewer") RegisteredUser uiReviewer, BindingResult bindingResult, //
			@ModelAttribute("unsavedReviewers") ArrayList<RegisteredUser> unsavedReviewers,//
			ModelMap modelMap) {

		checkAdminPermission(programme);

		if (bindingResult.hasErrors()) {
			return ASSIGN_REVIEWERS_TO_APPLICATION_SECTION;
		}

		RegisteredUser reviewer = userService.getUserByEmailIncludingDisabledAccounts(uiReviewer.getEmail());
		@SuppressWarnings("unchecked")
		List<RegisteredUser> availableRevs = (List<RegisteredUser>) modelMap.get("availableReviewers");
		if (availableRevs == null) {
			availableRevs = new ArrayList<RegisteredUser>();
		}
		if (reviewer == null) {
			reviewer = userService.createNewUserForProgramme(uiReviewer.getFirstName(), uiReviewer.getLastName(), uiReviewer.getEmail(), programme,
					Authority.REVIEWER);
			modelMap.put("message", getMessage("assignReviewer.newReviewer.created", reviewer.getUsername(), reviewer.getEmail()));
			availableRevs.add(reviewer);
		} else {
			if (reviewer.isReviewerInLatestReviewRoundOfApplicationForm(form)) {
				modelMap.put("message", getMessage("assignReviewer.reviewer.alreadyExistsInTheApplication", reviewer.getUsername(), reviewer.getEmail()));
			} else if (!programme.getProgramReviewers().contains(reviewer)) {
				reviewService.addUserToProgramme(programme, reviewer);
				modelMap.put("message", getMessage("assignReviewer.newReviewer.addedToProgramme", reviewer.getUsername(), reviewer.getEmail()));
				availableRevs.add(reviewer);
			} else {
				modelMap.put("message", getMessage("assignReviewer.newReviewer.alreadyInProgramme", reviewer.getUsername(), reviewer.getEmail()));
			}
		}

		if (unsavedReviewers != null) {
			modelMap.put("unsavedReviewers", unsavedReviewers);
		}
		return ASSIGN_REVIEWERS_TO_APPLICATION_SECTION;
	}
	
	
}
