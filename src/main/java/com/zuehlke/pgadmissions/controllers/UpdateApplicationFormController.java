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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@Controller
@RequestMapping("/update")
public class UpdateApplicationFormController {
	
	private static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "private/pgStudent/form/components/qualification_details";
	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudent/form/components/address_details";
	private final ApplicationsService applicationService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;

	
	UpdateApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public UpdateApplicationFormController(UserService userService, ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor) {
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.userService = userService;
	}
	
	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editPersonalDetails(@ModelAttribute PersonalDetails personalDetails, @RequestParam Integer id, @RequestParam Integer appId,
			BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		
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
		model.setApplicationForm(application);
		model.setPersonalDetails(personalDetails);
		model.setResult(result);
		modelMap.put("model", model);
		modelMap.put("formDisplayState", "open");

		return new ModelAndView("private/pgStudent/form/components/personal_details", modelMap);
	}
	
	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editQualification(@ModelAttribute QualificationDTO qual, @RequestParam Integer id, @RequestParam Integer appId, @RequestParam Integer qualId, BindingResult result) {
		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		
		RegisteredUser user = userService.getUser(id);
		Qualification qualification = applicationService.getQualificationById(qualId);
		QualificationValidator qualificationValidator = new QualificationValidator();
		qualificationValidator.validate(qual, result);
		
		if (!result.hasErrors()) {
			qualification.setDate_taken(qual.getDate_taken());
			qualification.setDegree(qual.getDegree());
			qualification.setGrade(qual.getGrade());
			qualification.setInstitution(qual.getInstitution());
			userService.saveQualification(qualification);
//			user.getQualifications().add(qualifichation); //hibernate exception: field degree doesn't have a default value
			userService.save(user);
		}
		
		
		PageModel model = new PageModel();
		model.setApplicationForm(application);
		model.setUser(user);
		model.setResult(result);
		return new ModelAndView(APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/addFunding", method = RequestMethod.POST)
	@Transactional
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);
		
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		
		FundingValidator fundingValidator = new FundingValidator();
		fundingValidator.validate(fund, result);
		if (!result.hasErrors()) {
			application.setFunding(fund.getFunding());
			applicationService.save(application);
		}
		
		RegisteredUser user = userService.getUser(id);

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setFunding(fund);
		model.setResult(result);
		modelMap.put("model", model);
		
		return new ModelAndView("private/pgStudent/form/components/funding_details", modelMap);
	}


	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		
		RegisteredUser user = userService.getUser(id);
		
		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		if (!result.hasErrors()) {
			user.setAddress(addr.getAddress());
			userService.save(user);
		}
		
		ApplicationPageModel model = new ApplicationPageModel();
		ApplicationForm applicationForm = application;
		model.setUser(user);
		model.setApplicationForm(applicationForm);
		model.setAddress(addr);
		model.setResult(result);
		modelMap.put("model", model);
		
		return new ModelAndView(APPLICATION_ADDRESS_APPLICANT_VIEW_NAME, modelMap);
	}
	
	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
}
