package com.zuehlke.pgadmissions.controllers.applicantform;

import java.beans.PropertyEditor;
import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/update")
public class FundingController {

	private static final String STUDENT_FUNDING_DETAILS_VIEW = "/private/pgStudents/form/components/funding_details";
	private final ApplicationsService applicationService;
	private final PropertyEditor datePropertyEditor;

	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;

	private final FundingValidator fundingValidator;
	private final FundingService fundingService;
	private final DocumentPropertyEditor documentPropertyEditor;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;
	private final ApplicationFormUserRoleService applicationFormUserRoleService;

	FundingController() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public FundingController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
			DatePropertyEditor datePropertyEditor, FundingValidator fundingValidator, FundingService fundingService,
			DocumentPropertyEditor documentPropertyEditor, UserService userService, EncryptionHelper encryptionHelper, ApplicationFormUserRoleService applicationFormUserRoleService) {
		this.applicationService = applicationsService;

		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.fundingValidator = fundingValidator;
		this.fundingService = fundingService;
		this.documentPropertyEditor = documentPropertyEditor;
		this.userService = userService;
		this.encryptionHelper = encryptionHelper;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
	}

	@InitBinder(value="funding")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(fundingValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
	}
	
	 public StringTrimmerEditor newStringTrimmerEditor() {
	     return new StringTrimmerEditor(false);
	 }

	@RequestMapping(value = "/editFunding", method = RequestMethod.POST)
	public String editFunding(@Valid Funding funding, BindingResult result) {

		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(funding.getApplication().isDecided()){
			throw new CannotUpdateApplicationException(funding.getApplication().getApplicationNumber());
		}
		if(result.hasErrors()){
			return STUDENT_FUNDING_DETAILS_VIEW;
		}
        ApplicationForm applicationForm = funding.getApplication();
        
		fundingService.save(funding);
		applicationService.save(applicationForm);
		applicationFormUserRoleService.registerApplicationUpdate(applicationForm, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
		return "redirect:/update/getFunding?applicationId=" + funding.getApplication().getApplicationNumber();
	}


	@RequestMapping(value = "/getFunding", method = RequestMethod.GET)
	public String getFundingView() {

		if (!userService.getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENT_FUNDING_DETAILS_VIEW;
	}

	@ModelAttribute
	public Funding getFunding(@RequestParam(value="fundingId", required=false) String encryptedFundingId) {
		if (StringUtils.isBlank(encryptedFundingId)) {
			return new Funding();	
		}
		Funding funding = fundingService.getFundingById(encryptionHelper.decryptToInteger(encryptedFundingId));
		if (funding == null) {
			throw new ResourceNotFoundException();
		}
		return funding;
	}

	@ModelAttribute("fundingTypes")
	public FundingType[] getFundingTypes() {		
		return FundingType.values();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {		
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if(application == null || !userService.getCurrentUser().canSee(application)){
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}

}