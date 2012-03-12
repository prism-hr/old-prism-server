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

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.ProgrammeDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.MessengerJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PhoneNumberJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@Controller
@RequestMapping("/update")
public class UpdateApplicationFormController {

	private static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/qualification_details";
	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/address_details";
	private static final String APPLICATION_EMPLOYMENT_POSITION_VIEW_NAME = "private/pgStudents/form/components/employment_position_details";
	private static final String APPLICATON_REFEREEE_VIEW_NAME = "private/pgStudents/form/components/references_details";
	private final ApplicationsService applicationService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountryService countryService;
	private final ProgrammeDetailDAO programmeDetailDAO;
	private final RefereeService refereeService;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor;
	private final MessengerJSONPropertyEditor messengerJSONPropertyEditor;

	private PersonalDetailDAO personalDetailDAO;

	UpdateApplicationFormController() {
		this(null, null, null, null, null, null, null, null, null, null, null);
	}
	
	

	@Autowired
	public UpdateApplicationFormController(UserService userService, ApplicationsService applicationService, UserPropertyEditor userPropertyEditor,
			DatePropertyEditor datePropertyEditor, CountryService countryService, PersonalDetailDAO personalDetailDAO, ProgrammeDetailDAO programmeDetailDAO
			, RefereeService refereeService, PhoneNumberJSONPropertyEditor phoneNumberJSONPropertyEditor, MessengerJSONPropertyEditor messengerJSONPropertyEditor,
			ApplicationFormPropertyEditor applicationFormPropertyEditor) {

		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.userService = userService;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;	
		this.personalDetailDAO = personalDetailDAO;
		this.programmeDetailDAO = programmeDetailDAO;
		this.refereeService = refereeService;
		this.phoneNumberJSONPropertyEditor = phoneNumberJSONPropertyEditor;
		this.messengerJSONPropertyEditor = messengerJSONPropertyEditor;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Telephone.class, phoneNumberJSONPropertyEditor);
		binder.registerCustomEditor(Messenger.class, messengerJSONPropertyEditor);
		
		

	}
	
	@RequestMapping(value = "/editProgramme", method = RequestMethod.POST)
	public ModelAndView editPersonalDetails(@ModelAttribute ProgrammeDetails programme, @RequestParam Integer id1, @RequestParam Integer appId1,
			BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId1);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		ProgrammeDetailsValidator personalDetailsValidator = new ProgrammeDetailsValidator();
		personalDetailsValidator.validate(programme, result);

		RegisteredUser user = userService.getUser(id1);
		if (!user.equals(SecurityContextHolder.getContext().getAuthentication().getDetails())) {
			throw new AccessDeniedException();
		}

		if (!result.hasErrors()) {
			@SuppressWarnings("deprecation")
			ProgrammeDetail pd = programmeDetailDAO.getProgrammeDetailWithApplication(application);
			if (pd == null) {
				pd = new ProgrammeDetail();
			}

			pd.setProgrammeName(programme.getProgrammeDetailsProgrammeName());
			pd.setProjectName(programme.getProgrammeDetailsProjectName());
			pd.setStartDate(programme.getProgrammeDetailsStartDate());
			pd.setReferrer(Referrer.fromString(programme.getProgrammeDetailsReferrer()));
			pd.setStudyOption(StudyOption.fromString(programme.getProgrammeDetailsStudyOption()));
			pd.setApplication(application);

			programmeDetailDAO.save(pd);

		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		model.setApplicationForm(application);
		model.setProgrammeDetails(programme);
		model.setStudyOptions(StudyOption.values());
		model.setReferrers(Referrer.values());
		model.setResult(result);
		modelMap.put("model", model);

		return new ModelAndView("private/pgStudents/form/components/programme_details", modelMap);
	}

	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	public ModelAndView editQualification(@ModelAttribute QualificationDTO qual, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result,
			ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		RegisteredUser user = userService.getUser(id);

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);

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
			qualification.setQualificationLevel(qual.getQualificationLevel());
			qualification.setQualificationProgramName(qual.getQualificationProgramName());
			qualification.setQualificationScore(qual.getQualificationScore());
			qualification.setQualificationStartDate(qual.getQualificationStartDate());
			qualification.setQualificationType(qual.getQualificationType());
			if (qual.getQualificationId() == null) {
				application.getQualifications().add(qualification);
				applicationService.save(application);
				model.setQualification(new QualificationDTO());
			} else {
				applicationService.update(qualification);
				application.getQualifications().remove(qualification);
				application.getQualifications().add(qualification);
				model.setQualification(new QualificationDTO());
			}
		} else {

			model.setQualification(qual);
		}

		modelMap.put("model", model);

		return new ModelAndView(APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, modelMap);
	}

	@RequestMapping(value = "/addFunding", method = RequestMethod.POST)
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		RegisteredUser user = userService.getUser(id);

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);

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

	@RequestMapping(value = "/addEmploymentPosition", method = RequestMethod.POST)
	public ModelAndView addEmploymentPosition(EmploymentPosition positionDto, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result,
			ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		RegisteredUser user = userService.getUser(id);

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);

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
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer id, @RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		RegisteredUser user = userService.getUser(id);

		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		ApplicationPageModel model = new ApplicationPageModel();
		ApplicationForm applicationForm = application;
		model.setUser(user);
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setCountries(countryService.getAllCountries());

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
	public ModelAndView editReferee(@ModelAttribute("refereeDetails") Referee refereeDetails,  BindingResult errors) {
		
		if (refereeDetails.getApplication() != null && refereeDetails.getApplication().isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		if (refereeDetails.getApplication() != null && refereeDetails.getApplication().getApplicant() != null
				&& !getCurrentUser().equals(refereeDetails.getApplication().getApplicant())) {
			throw new ResourceNotFoundException();
		}
		
		if (!errors.hasErrors()) {			
			refereeService.save(refereeDetails);
		} 
		
		if (refereeDetails.getApplication() != null) {
			refereeDetails.getApplication().setReferees(java.util.Arrays.asList(refereeDetails));
		}
		
		ApplicationPageModel applicationPageModel = new ApplicationPageModel();
		applicationPageModel.setApplicationForm(refereeDetails.getApplication());
		applicationPageModel.setUser(getCurrentUser());
		applicationPageModel.setResult(errors);
		applicationPageModel.setResidenceStatuses(ResidenceStatus.values());
		applicationPageModel.setGenders(Gender.values());
		applicationPageModel.setPhoneTypes(PhoneType.values());
		applicationPageModel.setReferee(new Referee());
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
