package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "application/address_applicant";
	private final ProjectDAO projectDAO;
	private final ApplicationsService applicationService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;

	ApplicationFormController() {
		this(null, null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProjectDAO projectDAO, ApplicationsService applicationService, UserService userService,
			UserPropertyEditor userPropertyEditor) {
		this.projectDAO = projectDAO;
		this.applicationService = applicationService;
		this.userService = userService;
		this.userPropertyEditor = userPropertyEditor;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	@Transactional
	public ModelAndView createNewApplicationForm(@RequestParam Integer project) {

		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

		Project proj = projectDAO.getProjectById(project);

		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProject(proj);
		applicationService.save(applicationForm);

		return new ModelAndView("redirect:/application", "id", applicationForm.getId());

	}

	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editPersonalDetails(@ModelAttribute PersonalDetails personalDetails, @RequestParam Integer id, @RequestParam Integer appId,
			BindingResult result, ModelMap modelMap) {

		PersonalDetailsValidator personalDetailsValidator = new PersonalDetailsValidator();
		personalDetailsValidator.validate(personalDetails, result);

		RegisteredUser user = userService.getUser(id);
		if (!user.equals(SecurityContextHolder.getContext().getAuthentication().getDetails())) {
			throw new AccessDeniedException();
		}

		if (!result.hasErrors()) {
			user.setLastName(personalDetails.getLastName());
			user.setFirstName(personalDetails.getFirstName());
			user.setEmail(personalDetails.getEmail());
			userService.save(user);
		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = applicationService.getApplicationById(appId);
		model.setApplicationForm(applicationForm);
		model.setPersonalDetails(personalDetails);
		model.setResult(result);
		modelMap.put("model", model);
		modelMap.put("formDisplayState", "open");

		return new ModelAndView("application/personal_details_applicant", modelMap);
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitApplication(@RequestParam Integer applicationForm) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm appForm = applicationService.getApplicationById(applicationForm);
		if (appForm == null || !user.equals(appForm.getApplicant()) || appForm.isSubmitted()) {
			throw new ResourceNotFoundException();
		}
		appForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationService.save(appForm);
		return new ModelAndView("redirect:/applications?submissionSuccess=true");

	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@RequestMapping(value = "/addFunding", method = RequestMethod.POST)
	@Transactional
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result) {
		RegisteredUser user = userService.getUser(id);
		ApplicationForm application = applicationService.getApplicationById(appId);
		FundingValidator fundingValidator = new FundingValidator();
		fundingValidator.validate(fund, result);
		if (!result.hasErrors()) {
			application.setFunding(fund.getFunding());
			applicationService.save(application);
		}

		PageModel model = new PageModel();
		model.setApplicationForm(application);
		model.setUser(user);

		return new ModelAndView("application/funding_applicant", "model", model);
	}


	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result) {
		RegisteredUser user = userService.getUser(id);
		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		if (!result.hasErrors()) {
			user.setAddress(addr.getAddress());
			userService.save(user);
		}
		PageModel model = new PageModel();
		model.setApplicationForm(applicationService.getApplicationById(appId));
		model.setUser(user);

		return new ModelAndView(APPLICATION_ADDRESS_APPLICANT_VIEW_NAME, "model", model);
	}

}
