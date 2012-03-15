package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.dto.AdditionalInformation;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.MessengerJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@Controller
@RequestMapping("/update")
public class UpdateApplicationFormController {

	private static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/qualification_details";
	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/address_details";
	private static final String APPLICATION_EMPLOYMENT_POSITION_VIEW_NAME = "private/pgStudents/form/components/employment_position_details";
	private static final String APPLICATON_REFEREEE_VIEW_NAME = "private/pgStudents/form/components/references_details";
	private final ApplicationsService applicationService;
	private final UserPropertyEditor userPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final RefereeService refereeService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor;
	private final MessengerJSONPropertyEditor messengerJSONPropertyEditor;
	private final RefereeValidator refereeValidator;
	private final LanguageService languageService;
	private final LanguagePropertyEditor languagePropertyEditor;

	UpdateApplicationFormController() {
		this(null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public UpdateApplicationFormController(ApplicationsService applicationService, UserPropertyEditor userPropertyEditor,
			DatePropertyEditor datePropertyEditor, CountryService countryService, RefereeService refereeService,
			PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor, MessengerJSONPropertyEditor messengerJSONPropertyEditor,
			ApplicationFormPropertyEditor applicationFormPropertyEditor, RefereeValidator refereeValidator,
			LanguageService languageService, LanguagePropertyEditor languagePropertyEditor) {

		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;
		this.refereeService = refereeService;
		this.languagePropertyEditor = languagePropertyEditor;
		this.phoneNumberJSONPropertyEditor = phoneNumberJSONPropertyEditor;
		this.messengerJSONPropertyEditor = messengerJSONPropertyEditor;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.refereeValidator = refereeValidator;
		this.languageService = languageService;	
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditor);
		binder.registerCustomEditor(Messenger.class, messengerJSONPropertyEditor);
		binder.registerCustomEditor(Language.class, languagePropertyEditor);
	}

	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	public ModelAndView editQualification(@ModelAttribute QualificationDTO qual, @RequestParam Integer appId, BindingResult result,
			ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(getCurrentUser());
		model.setApplicationForm(application);
		model.setResult(result);
		model.setLanguages(languageService.getAllLanguages());
		model.setQualificationLevels(QualificationLevel.values());

		QualificationValidator qualificationValidator = new QualificationValidator();
		qualificationValidator.validate(qual, result);
		if (!result.hasErrors()) {
			Qualification qualification;
			if (qual.getQualificationId() == null) {
				qualification = new Qualification();
			} else {
				qualification = applicationService.getQualificationById(qual.getQualificationId());
			}

			qualification.setApplication(application);
			qualification.setQualificationAwardDate(qual.getQualificationAwardDate());
			qualification.setQualificationGrade(qual.getQualificationGrade());
			qualification.setQualificationInstitution(qual.getQualificationInstitution());
			qualification.setQualificationLanguage(qual.getQualificationLanguage());
			qualification.setQualificationLevel(QualificationLevel.fromString(qual.getQualificationLevel()));
			qualification.setQualificationProgramName(qual.getQualificationProgramName());
			qualification.setQualificationScore(qual.getQualificationScore());
			qualification.setQualificationStartDate(qual.getQualificationStartDate());
			qualification.setQualificationType(qual.getQualificationType());
			if (qual.getQualificationId() == null) {
				application.getQualifications().add(qualification);
			} 
				applicationService.save(application);
				model.setQualification(new QualificationDTO());
			
		} else {
			model.setQualification(qual);
		}

		modelMap.put("model", model);

		return new ModelAndView(APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, modelMap);
	}

	@RequestMapping(value = "/addFunding", method = RequestMethod.POST)
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(getCurrentUser());
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setFundingTypes(FundingType.values());

		FundingValidator fundingValidator = new FundingValidator();
		fundingValidator.validate(fund, result);
		if (!result.hasErrors()) {
			com.zuehlke.pgadmissions.domain.Funding funding;
			if (fund.getFundingId() == null) {
				funding = new com.zuehlke.pgadmissions.domain.Funding();
			} else {
				funding = applicationService.getFundingById(fund.getFundingId());
			}
			funding.setApplication(application);
			funding.setType(fund.getFundingType());
			funding.setDescription(fund.getFundingDescription());
			funding.setValue(fund.getFundingValue());
			funding.setAwardDate(fund.getFundingAwardDate());
			if (fund.getFundingId() == null) {
				application.getFundings().add(funding);
			}
			applicationService.save(application);
			model.setFunding(new Funding());
		} else {
			model.setFunding(fund);
		}
		modelMap.put("model", model);

		return new ModelAndView("private/pgStudents/form/components/funding_details", modelMap);
	}
	
	@RequestMapping(value = "/addAdditionalInformation", method = RequestMethod.POST)
	public ModelAndView addAdditionalInfo(@ModelAttribute AdditionalInformation additionalInformation, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(getCurrentUser());
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		
		AdditionalInformationValidator validator = new AdditionalInformationValidator();
		validator.validate(additionalInformation, result);
		
		if (!result.hasErrors()) {
			application.setAdditionalInformation(additionalInformation.getAdditionalInformation());
			applicationService.save(application);
		}
		modelMap.put("model", model);

		return new ModelAndView("private/pgStudents/form/components/additional_information", modelMap);
	}

	@RequestMapping(value = "/addEmploymentPosition", method = RequestMethod.POST)
	public ModelAndView addEmploymentPosition(EmploymentPosition positionDto, @RequestParam Integer appId, BindingResult result,
			ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(getCurrentUser());
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setLanguages(languageService.getAllLanguages());

		EmploymentPositionValidator positionValidator = new EmploymentPositionValidator();
		positionValidator.validate(positionDto, result);
		if (!result.hasErrors()) {
			com.zuehlke.pgadmissions.domain.EmploymentPosition position;
			if (positionDto.getPositionId() == null) {
				position = new com.zuehlke.pgadmissions.domain.EmploymentPosition();
			} else {
				position = applicationService.getEmploymentPositionById(positionDto.getPositionId());
			}
			position.setPosition_employer(positionDto.getPosition_employer());
			position.setPosition_endDate(positionDto.getPosition_endDate());
			position.setPosition_language(positionDto.getPosition_language());
			position.setPosition_remit(positionDto.getPosition_remit());
			position.setPosition_startDate(positionDto.getPosition_startDate());
			position.setPosition_title(positionDto.getPosition_title());
			if (positionDto.getPositionId() == null) {
				application.getEmploymentPositions().add(position);
			}
			applicationService.save(application);
			model.setEmploymentPosition(new EmploymentPosition());
		} else {
			model.setEmploymentPosition(positionDto);
		}
		modelMap.put("model", model);
		return new ModelAndView(APPLICATION_EMPLOYMENT_POSITION_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		ApplicationPageModel model = new ApplicationPageModel();
		ApplicationForm applicationForm = application;
		model.setUser(getCurrentUser());
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setCountries(countryService.getAllCountries());
		model.setAddressPurposes(AddressPurpose.values());

		if (!result.hasErrors()) {
			com.zuehlke.pgadmissions.domain.Address address;
			if (addr.getAddressId() == null) {
				address = new com.zuehlke.pgadmissions.domain.Address();
			} else {
				address = applicationService.getAddressById(addr.getAddressId());
			}

			address.setApplication(application);
			address.setLocation(addr.getAddressLocation());
			address.setPostCode(addr.getAddressPostCode());
			address.setCountry(addr.getAddressCountry());
			address.setPurpose(addr.getAddressPurpose());
			address.setStartDate(addr.getAddressStartDate());
			address.setEndDate(addr.getAddressEndDate());
			address.setContactAddress(AddressStatus.fromString(addr.getAddressContactAddress()));

			if (addr.getAddressId() == null) {
				application.getAddresses().add(address);
			}
			applicationService.save(application);
			model.setAddress(new Address());
		} else {
			model.setAddress(addr);
		}
		modelMap.put("model", model);

		return new ModelAndView(APPLICATION_ADDRESS_APPLICANT_VIEW_NAME, modelMap);
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	Qualification newQualification() {
		return new Qualification();
	}

	@RequestMapping(value = "/refereeDetails", method = RequestMethod.POST)
	public ModelAndView editReferee(@ModelAttribute("refereeDetails") Referee refereeDetails, BindingResult errors) {

		if (refereeDetails.getApplication() != null && refereeDetails.getApplication().isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		if (refereeDetails.getApplication() != null && refereeDetails.getApplication().getApplicant() != null
				&& !getCurrentUser().equals(refereeDetails.getApplication().getApplicant())) {
			throw new ResourceNotFoundException();
		}
		ApplicationPageModel applicationPageModel = new ApplicationPageModel();
		refereeValidator.validate(refereeDetails, errors);
		if (!errors.hasErrors()) {
			refereeService.save(refereeDetails);
			applicationPageModel.setReferee(new Referee());
		} else {
			applicationPageModel.setReferee(refereeDetails);
		}

		applicationPageModel.setApplicationForm(refereeDetails.getApplication());
		applicationPageModel.setUser(getCurrentUser());
		applicationPageModel.setResult(errors);
		applicationPageModel.setResidenceStatuses(ResidenceStatus.values());
		applicationPageModel.setGenders(Gender.values());
		applicationPageModel.setPhoneTypes(PhoneType.values());
		applicationPageModel.setCountries(countryService.getAllCountries());
		applicationPageModel.setLanguages(languageService.getAllLanguages());
		ModelAndView modelAndView = new ModelAndView(APPLICATON_REFEREEE_VIEW_NAME, "model", applicationPageModel);
		modelAndView.addObject("formDisplayState", "open");
		return modelAndView;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@ModelAttribute("refereeDetails")
	public Referee getRefereeDetails(Integer refereeId) {
		if (refereeId == null) {
			return newReferee();
		}
		Referee refereeDetails = refereeService.getRefereeById(refereeId);
		if (refereeDetails == null) {
			throw new ResourceNotFoundException();
		}
		return refereeDetails;
	}

	Referee newReferee() {
		return new Referee();
	}

}
