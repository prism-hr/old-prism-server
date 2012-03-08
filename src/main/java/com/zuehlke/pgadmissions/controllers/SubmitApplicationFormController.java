package com.zuehlke.pgadmissions.controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.DTOUtils;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@Controller
@RequestMapping("/submit")
public class SubmitApplicationFormController {

	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;
	private CountriesDAO countriesDAO;
	private static final String VIEW_APPLICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/main_application_page";

	SubmitApplicationFormController() {
		this(null, null, null);
	}

	@Autowired
	public SubmitApplicationFormController(ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor, CountriesDAO countriesDAO) {
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.countriesDAO = countriesDAO;
	}


	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView submitApplication(@ModelAttribute ApplicationFormDetails appForm, @RequestParam Integer applicationFormId, BindingResult result) {
		ApplicationForm applicationForm = getApplicationForm(applicationFormId);
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (applicationForm == null || !user.equals(applicationForm.getApplicant()) || applicationForm.isSubmitted()) {
			throw new ResourceNotFoundException();
		}

		appForm.setNumberOfAddresses(applicationForm.getAddresses().size());
		
		int numberOfContactAddresses = 0;
		for (com.zuehlke.pgadmissions.domain.Address address : applicationForm.getAddresses()) {
			if (address.getContactAddress() == AddressStatus.YES) {
				numberOfContactAddresses ++;
			}
		}
		
		appForm.setNumberOfContactAddresses(numberOfContactAddresses);
		
		ApplicationFormValidator validator = new ApplicationFormValidator();
		
		validator.validate(appForm, result);
		List<FieldError> fieldErrors = new LinkedList<FieldError>();
		fieldErrors.addAll(result.getFieldErrors());
		if (result.hasErrors()) {
			ApplicationPageModel viewApplicationModel = new ApplicationPageModel();

			viewApplicationModel.setApplicationForm(applicationForm);
			viewApplicationModel.setPersonalDetails(DTOUtils.createPersonalDetails(applicationForm));
			viewApplicationModel.setAddress(new Address());
			viewApplicationModel.setFunding(new com.zuehlke.pgadmissions.dto.Funding());
			viewApplicationModel.setMessage("Some required fields are missing, please review your application form.");
			viewApplicationModel.setResult(result);
			viewApplicationModel.setUser(user);
			viewApplicationModel.setCountries(countriesDAO.getAllCountries());
			
			return new ModelAndView(VIEW_APPLICATION_APPLICANT_VIEW_NAME,"model", viewApplicationModel);
			
		}

		applicationForm.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		applicationService.save(applicationForm);
		return new ModelAndView("redirect:/applications?submissionSuccess=true");

	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	private ApplicationForm getApplicationForm(Integer applicationFormId) {
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		ApplicationForm applicationForm = applicationService.getApplicationById(applicationFormId);
		if(applicationForm == null || !user.canSee(applicationForm)){
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}
}
