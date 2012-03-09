package com.zuehlke.pgadmissions.controllers;

import java.util.Date;

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

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.ProgrammeDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.dto.Referee;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@Controller
@RequestMapping("/update")
public class UpdateApplicationFormController {

	private static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/qualification_details";
	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/address_details";
	private static final String APPLICATION_EMPLOYMENT_POSITION_VIEW_NAME = "private/pgStudents/form/components/employment_position_details";
	private static final String APPLICATON_REFEREEE_VIEW_NAME =  "private/pgStudents/form/components/referee_details";;
	private final ApplicationsService applicationService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;
	private final DatePropertyEditor datePropertyEditor;
	private final CountriesDAO countriesDAO;
	private final PersonalDetailDAO personalDetailDAO;
	private final ProgrammeDetailDAO programmeDetailDAO;


	UpdateApplicationFormController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public UpdateApplicationFormController(UserService userService, ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor, DatePropertyEditor datePropertyEditor, 
			CountriesDAO countriesDAO, PersonalDetailDAO personalDetailDAO, ProgrammeDetailDAO programmeDetailDAO) {

		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.userService = userService;
		this.datePropertyEditor = datePropertyEditor;
		this.countriesDAO = countriesDAO;
		this.personalDetailDAO = personalDetailDAO;
		this.programmeDetailDAO = programmeDetailDAO;

	}

	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editPersonalDetails(@ModelAttribute PersonalDetails personalDetails, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

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
			PersonalDetail ps = personalDetailDAO.getPersonalDetailWithApplication(application);
			if (ps == null) {
				ps = new PersonalDetail();
			}

			ps.setFirstName(personalDetails.getFirstName());
			ps.setLastName(personalDetails.getLastName());
			ps.setEmail(personalDetails.getEmail());
			ps.setGender(Gender.fromString(personalDetails.getGender()));
			ps.setDateOfBirth(personalDetails.getDateOfBirth());
			ps.setCountry(countriesDAO.getCountryWithName(personalDetails.getCountry()));
			ps.setResidenceCountry(countriesDAO.getCountryWithName(personalDetails.getResidenceCountry()));
			ps.setResidenceStatus(personalDetails.getResidenceStatus());
			ps.setApplication(application);

			personalDetailDAO.save(ps);

		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		model.setApplicationForm(application);
		model.setPersonalDetails(personalDetails);
		model.setResult(result);
		model.setResidenceStatuses(ResidenceStatus.values());
		model.setCountries(countriesDAO.getAllCountries());
		modelMap.put("model", model);
		modelMap.put("formDisplayState", "open");

		return new ModelAndView("private/pgStudents/form/components/personal_details", modelMap);
	}

	@RequestMapping(value = "/editProgramme", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editPersonalDetails(@ModelAttribute ProgrammeDetails programme, @RequestParam Integer id1,
			@RequestParam Integer appId1, BindingResult result, ModelMap modelMap) {

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
			ProgrammeDetail pd =  programmeDetailDAO.getProgrammeDetailWithApplication(application);
			if (pd == null) {
				pd = new ProgrammeDetail();
			}

			pd.setProgrammeName(programme.getProgrammeDetailsProgrammeName());
			pd.setProjectName(programme.getProgrammeDetailsProjectName());
			pd.setStartDate(programme.getProgrammeDetailsStartDate());
			pd.setReferrer(programme.getProgrammeDetailsReferrer());
			pd.setStudyOption(programme.getProgrammeDetailsStudyOption());
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
	public ModelAndView editQualification(@ModelAttribute QualificationDTO qual, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

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
	@Transactional
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer id, @RequestParam Integer appId,
			BindingResult result, ModelMap modelMap) {
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
	@Transactional
	public ModelAndView addEmploymentPosition(EmploymentPosition positionDto, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

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

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);

	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

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
		model.setCountries(countriesDAO.getAllCountries());

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
			address.setContactAddress(addr.getAddressContactAddress());
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

	@RequestMapping(value = "/addReferee", method = RequestMethod.POST)
	@Transactional
	public ModelAndView addReferee(@ModelAttribute Referee ref, @RequestParam Integer id, @RequestParam Integer appId,
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
		//		model.setResult(result);

		com.zuehlke.pgadmissions.domain.Referee referee;
		if (ref.getRefereeId() == null) {
			referee = new com.zuehlke.pgadmissions.domain.Referee();
		} else {
			referee = applicationService.getRefereeById(ref.getRefereeId());
		}
		referee.setApplication(application);
		referee.setAddressCountry(ref.getAddressCountry());
		referee.setAddressLocation(ref.getAddressLocation());
		referee.setAddressPostcode(ref.getAddressPostcode());
		referee.setEmail(ref.getEmail());
		referee.setFirstname(ref.getFirstname());
		referee.setJobEmployer(ref.getJobEmployer());
		referee.setJobTitle(ref.getJobTitle());
		referee.setLastname(ref.getLastname());
		referee.setMessengers(ref.getMessengers());
		referee.setRelationship(ref.getRelationship());
		referee.setTelephones(ref.getTelephones());
		if (ref.getRefereeId() == null) {
			application.getReferees().add(referee);
		}
		applicationService.save(application);
		model.setReferee(new Referee());
		modelMap.put("model", model);

		return new ModelAndView(APPLICATON_REFEREEE_VIEW_NAME, "model", model);
	}

}
