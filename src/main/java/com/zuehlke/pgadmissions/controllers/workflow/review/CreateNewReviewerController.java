package com.zuehlke.pgadmissions.controllers.workflow.review;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
import com.zuehlke.pgadmissions.validators.ReviewRoundValidator;
@Controller
@RequestMapping("/review")
public class CreateNewReviewerController extends ReviewController {

	private static final String REDIRECT_REVIEW_ASSIGN_REVIEWERS = "redirect:/review/assignReviewers";
	private static final String REDIRECT_REVIEW_MOVE_TO_REVIEW = "redirect:/review/moveToReview";

	CreateNewReviewerController() {
		this(null, null, null, null, null,null,null, null);
	}

	@Autowired
	public CreateNewReviewerController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator reviewerValidator, ReviewRoundValidator reviewRoundValidator,
			ReviewService reviewService, MessageSource messageSource, ReviewerPropertyEditor reviewerPropertyEditor, EncryptionHelper encryptionHelper) {
		super(applicationsService, userService, reviewerValidator, reviewRoundValidator, reviewService, messageSource, reviewerPropertyEditor, encryptionHelper);

	}

	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public ModelAndView createReviewerForNewReviewRound(@Valid @ModelAttribute("reviewer") RegisteredUser reviewer, BindingResult bindingResult,
			@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("pendingReviewers") List<RegisteredUser> pendingReviewers,
			@ModelAttribute("previousReviewers") List<RegisteredUser> previousReviewers) {
		return createNewReviewer(reviewer, bindingResult, applicationForm, pendingReviewers, previousReviewers, REDIRECT_REVIEW_MOVE_TO_REVIEW);
	}

	@RequestMapping(value = "/assignNewReviewer", method = RequestMethod.POST)
	public ModelAndView createReviewerForExistingReviewRound(@Valid @ModelAttribute("reviewer") RegisteredUser reviewer, BindingResult bindingResult,
			@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("pendingReviewers") List<RegisteredUser> pendingReviewers,
			@ModelAttribute("previousReviewers") List<RegisteredUser> previousReviewers) {

		return createNewReviewer(reviewer, bindingResult, applicationForm, pendingReviewers, previousReviewers, REDIRECT_REVIEW_ASSIGN_REVIEWERS);
	}

	private ModelAndView createNewReviewer(RegisteredUser reviewer, BindingResult bindingResult, ApplicationForm applicationForm,
			List<RegisteredUser> pendingReviewers, List<RegisteredUser> previousReviewers, String viewName) {
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView(REVIEW_DETAILS_VIEW_NAME);
			if(REDIRECT_REVIEW_MOVE_TO_REVIEW.equals(viewName) ){
				modelAndView.getModel().put("assignOnly", false);
			}else{
				modelAndView.getModel().put("assignOnly", true);
			}
			
			return modelAndView;
		}
		List<String> newUserIds = new ArrayList<String>();
		for (RegisteredUser registeredUser : pendingReviewers) {			
			newUserIds.add(encryptionHelper.encrypt(registeredUser.getId()));
		}

		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(reviewer.getEmail());
		if (existingUser != null) {

			if (existingUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm)) {
				return getCreateReviewerModelAndView(applicationForm, newUserIds,
						getCreateReviewerMessage("assignReviewer.user.alreadyExistsInTheApplication", existingUser), viewName);
			}

			if (pendingReviewers.contains(existingUser)) {
				return getCreateReviewerModelAndView(applicationForm, newUserIds, getCreateReviewerMessage("assignReviewer.user.pending", existingUser),
						viewName);
			}

			if (previousReviewers.contains(existingUser)) {
				newUserIds.add(encryptionHelper.encrypt(existingUser.getId()));
				return getCreateReviewerModelAndView(applicationForm, newUserIds, getCreateReviewerMessage("assignReviewer.user.previous", existingUser),
						viewName);
			}

			if (applicationForm.getProgram().getProgramReviewers().contains(existingUser)) {
				newUserIds.add(encryptionHelper.encrypt(existingUser.getId()));
				return getCreateReviewerModelAndView(applicationForm, newUserIds,
						getCreateReviewerMessage("assignReviewer.user.alreadyInProgramme", existingUser), viewName);
			}
			
			newUserIds.add(encryptionHelper.encrypt(existingUser.getId()));
			userService.updateUserWithNewRoles(existingUser, applicationForm.getProgram(), Authority.REVIEWER);
			return getCreateReviewerModelAndView(applicationForm, newUserIds, getCreateReviewerMessage("assignReviewer.user.added", existingUser), viewName);

		}
		RegisteredUser newUser = userService.createNewUserForProgramme(reviewer.getFirstName(), reviewer.getLastName(), reviewer.getEmail(), applicationForm.getProgram(),
				Authority.REVIEWER);
		userService.setDirectURLAndSaveUser(DirectURLsEnum.ADD_REVIEW, applicationForm, newUser);
//		RegisteredUser newUser = userService.createNewUserInRole(reviewer.getFirstName(), reviewer.getLastName(), reviewer.getEmail(), Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, applicationForm);
		newUserIds.add(encryptionHelper.encrypt(newUser.getId()));
		return getCreateReviewerModelAndView(applicationForm, newUserIds, getCreateReviewerMessage("assignReviewer.user.created", newUser), viewName);
	}

	private ModelAndView getCreateReviewerModelAndView(ApplicationForm applicationForm, List<String> newUserIds, String message, String viewName) {

		ModelAndView modelAndView = new ModelAndView(viewName);
		modelAndView.getModel().put("applicationId", applicationForm.getApplicationNumber());
		modelAndView.getModel().put("pendingReviewer", newUserIds);
		modelAndView.getModel().put("message", message);
		return modelAndView;
	}

	@Override
	@ModelAttribute("reviewRound")
	public ReviewRound getReviewRound(Object applicationId) {

		return new ReviewRound();
	}
}
