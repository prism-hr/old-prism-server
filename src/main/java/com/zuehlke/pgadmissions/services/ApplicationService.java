package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.FundingSource;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.Title;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceReportListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationLanguageQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPassportDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;

@Service
@Transactional
public class ApplicationService {

    public static final int APPLICATION_BLOCK_SIZE = 50;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private Mapper mapper;

    public Application getById(int id) {
        return entityService.getById(Application.class, id);
    }

    public Application getOrCreate(final User user, Advert advert) {
        Application transientApplication = new Application().withUser(user).withParentResource(advert).withCreatedTimestamp(new DateTime());
        Application persistentApplication = entityService.getDuplicateEntity(transientApplication);
        if (persistentApplication == null) {
            Application previousApplication = applicationDAO.getPreviousSubmittedApplication(transientApplication);
            if (previousApplication == null) {
                previousApplication = applicationDAO.getPreviousUnsubmittedApplication(transientApplication);
            }
            if (previousApplication != null) {
                applicationCopyHelper.copyApplicationFormData(transientApplication, previousApplication);
                entityService.save(transientApplication);
                persistentApplication = transientApplication;
            }
        }
        return persistentApplication;
    }

    public void save(Application application) {
        entityService.save(application);
    }

    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }

    public List<ResourceConsoleListRowDTO> getConsoleListBlock(Integer page, Integer perPage) {
        return resourceService.getConsoleListBlock(Application.class, page, perPage);
    }

    public List<ResourceReportListRowDTO> getReportList() {
        return resourceService.getReportList(Application.class);
    }

    public LocalDate getEarliestStartDate(Application application) {
        LocalDate currentDate = new LocalDate();
        LocalDate earliestStartDate = programService.getEarliestProgramInstance(application).getApplicationStartDate();
        if (earliestStartDate.isBefore(currentDate)) {
            return getNextMonday(currentDate);
        }
        return getNextMonday(earliestStartDate);
    }

    public LocalDate getLatestStartDate(Application application) {
        LocalDate latestStartDate = programService.getLatestProgramInstance(application).getApplicationDeadline();
        return getNextMonday(latestStartDate);
    }

    public LocalDate getRecommendedStartDate(Application application) {
        LocalDate recommendedStartDate = null;
        if (application.getAdvert().isImmediateStart()) {
            recommendedStartDate = new LocalDate().plusWeeks(4);
        } else {
            recommendedStartDate = programService.getEarliestProgramInstance(application).getApplicationDeadline();
        }
        recommendedStartDate = getNextMonday(recommendedStartDate);
        LocalDate latestStartDate = getLatestStartDate(application);
        return recommendedStartDate.isAfter(latestStartDate) ? latestStartDate : recommendedStartDate;
    }

    public String getApplicationExportReference(Application application) {
        return applicationDAO.getApplicationExportReference(application);
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return applicationDAO.getApplicationCreatorIpAddress(application);
    }

    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return applicationDAO.getPrimarySupervisor(offerRecommendationComment);
    }

    public List<ApplicationReferee> getApplicationExportReferees(Application application) {
        return applicationDAO.getApplicationExportReferees(application);
    }

    public List<ApplicationReferee> setApplicationExportReferees(Application application) {
        PrismStateGroup stateGroup = application.getState().getStateGroup().getId();
        if (stateGroup == PrismStateGroup.APPLICATION_REJECTED || stateGroup == PrismStateGroup.APPLICATION_WITHDRAWN) {
            if (getApplicationExportReferees(application).size() < 2) {
                Set<Integer> refereesToSend = Sets.newHashSet();

                for (ApplicationReferee referee : application.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }

                    if (BooleanUtils.isTrue(referee.isIncludeInExport())) {
                        refereesToSend.add(referee.getId());
                    } else if (referee.getComment() != null && !referee.getComment().isDeclinedResponse()) {
                        referee.setIncludeInExport(true);
                        refereesToSend.add(referee.getId());
                    }
                }

                for (ApplicationReferee referee : application.getReferees()) {
                    if (refereesToSend.size() == 2) {
                        break;
                    }

                    if (!refereesToSend.contains(referee.getId())) {
                        referee.setIncludeInExport(true);
                        refereesToSend.add(referee.getId());
                    }
                }
            }
        }
        return getApplicationExportReferees(application);
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return applicationDAO.getApplicationExportQualification(application);
    }

    public List<Application> getUclApplicationsForExport() {
        return applicationDAO.getUclApplicationsForExport();
    }

    public void saveProgramDetails(Integer applicationId, ApplicationProgramDetailsDTO programDetailsDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();
        ApplicationProgramDetails programDetails = application.getProgramDetails();
        if (programDetails == null) {
            programDetails = new ApplicationProgramDetails();
            application.setProgramDetails(programDetails);
        }

        StudyOption studyOption = importedEntityService.getById(StudyOption.class, institution, programDetailsDTO.getStudyOption());
        ReferralSource referralSource = importedEntityService.getById(ReferralSource.class, institution, programDetailsDTO.getReferralSource());
        programDetails.setStudyOption(studyOption);
        programDetails.setStartDate(programDetailsDTO.getStartDate().toLocalDate());
        programDetails.setReferralSource(referralSource);

        // TODO replace supervisors
        // programDetails.getSupervisors().clear();
        // for (ApplicationSupervisorDTO supervisorDTO : programDetailsDTO.getSupervisors()) {
        // }
    }

    public void savePersonalDetails(Integer applicationId, ApplicationPersonalDetailsDTO personalDetailsDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new ApplicationPersonalDetails();
            application.setPersonalDetails(personalDetails);
        }

        User user = application.getUser();
        user.setFirstName(personalDetailsDTO.getUser().getFirstName());
        user.setFirstName2(Strings.emptyToNull(personalDetailsDTO.getUser().getFirstName2()));
        user.setFirstName3(Strings.emptyToNull(personalDetailsDTO.getUser().getFirstName3()));
        user.setLastName(personalDetailsDTO.getUser().getLastName());

        Title title = importedEntityService.getById(Title.class, institution, personalDetailsDTO.getTitle());
        Gender gender = importedEntityService.getById(Gender.class, institution, personalDetailsDTO.getGender());
        Country country = importedEntityService.getById(Country.class, institution, personalDetailsDTO.getCountry());
        Language firstNationality = importedEntityService.getById(Language.class, institution, personalDetailsDTO.getFirstNationality());
        Language secondNationality = personalDetailsDTO.getSecondNationality() != null ? importedEntityService.<Language> getById(Language.class, institution,
                personalDetailsDTO.getSecondNationality()) : null;
        Domicile residenceCountry = importedEntityService.getById(Domicile.class, institution, personalDetailsDTO.getResidenceCountry());
        Ethnicity ethnicity = importedEntityService.getById(Ethnicity.class, institution, personalDetailsDTO.getEthnicity());
        Disability disability = importedEntityService.getById(Disability.class, institution, personalDetailsDTO.getDisability());
        personalDetails.setTitle(title);
        personalDetails.setGender(gender);
        personalDetails.setDateOfBirth(personalDetailsDTO.getDateOfBirth().toLocalDate());
        personalDetails.setCountry(country);
        personalDetails.setFirstNationality(firstNationality);
        personalDetails.setSecondNationality(secondNationality);
        personalDetails.setFirstLanguageEnglish(personalDetailsDTO.getFirstLanguageEnglish());
        personalDetails.setResidenceCountry(residenceCountry);
        personalDetails.setVisaRequired(personalDetailsDTO.getVisaRequired());
        personalDetails.setPhoneNumber(personalDetailsDTO.getPhoneNumber());
        personalDetails.setMessenger(Strings.emptyToNull(personalDetailsDTO.getMessenger()));
        personalDetails.setEthnicity(ethnicity);
        personalDetails.setDisability(disability);

        ApplicationLanguageQualificationDTO languageQualificationDTO = personalDetailsDTO.getLanguageQualification();
        if (languageQualificationDTO == null) {
            personalDetails.setLanguageQualification(null);
        } else {
            ApplicationLanguageQualification languageQualification = personalDetails.getLanguageQualification();
            if (languageQualification == null) {
                languageQualification = new ApplicationLanguageQualification();
                personalDetails.setLanguageQualification(languageQualification);
            }
            LanguageQualificationType languageQualificationType = importedEntityService.getById(LanguageQualificationType.class, institution,
                    languageQualificationDTO.getType());
            Document proofOfAward = entityService.getById(Document.class, languageQualificationDTO.getProofOfAward().getId());
            languageQualification.setType(languageQualificationType);
            languageQualification.setExamDate(languageQualificationDTO.getExamDate().toLocalDate());
            languageQualification.setOverallScore(languageQualificationDTO.getOverallScore());
            languageQualification.setReadingScore(languageQualificationDTO.getReadingScore());
            languageQualification.setWritingScore(languageQualificationDTO.getWritingScore());
            languageQualification.setSpeakingScore(languageQualificationDTO.getSpeakingScore());
            languageQualification.setListeningScore(languageQualificationDTO.getListeningScore());
            languageQualification.setProofOfAward(proofOfAward);
        }

        ApplicationPassportDTO passportDTO = personalDetailsDTO.getPassport();
        if (passportDTO == null) {
            personalDetails.setPassport(null);
        } else {
            ApplicationPassport passport = personalDetails.getPassport();
            if (passport == null) {
                passport = new ApplicationPassport();
                personalDetails.setPassport(passport);
            }
            passport.setNumber(passportDTO.getNumber());
            passport.setName(passportDTO.getName());
            passport.setIssueDate(passportDTO.getIssueDate().toLocalDate());
            passport.setExpiryDate(passportDTO.getExpiryDate().toLocalDate());
        }
    }

    public void saveAddress(Integer applicationId, ApplicationAddressDTO addressDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();

        ApplicationAddress address = application.getAddress();
        if (address == null) {
            address = new ApplicationAddress();
            application.setAddress(address);
        }

        AddressDTO currentAddressDTO = addressDTO.getCurrentAddress();
        Address currentAddress = address.getCurrentAddress();
        if (currentAddress == null) {
            currentAddress = new Address();
            address.setCurrentAddress(currentAddress);
        }
        copyAddress(institution, currentAddress, currentAddressDTO);

        AddressDTO contactAddressDTO = addressDTO.getContactAddress();
        Address contactAddress = address.getContactAddress();
        if (contactAddress == null) {
            contactAddress = new Address();
            address.setContactAddress(contactAddress);
        }
        copyAddress(institution, contactAddress, contactAddressDTO);
    }

    public ApplicationQualification saveQualification(Integer applicationId, Integer qualificationId, ApplicationQualificationDTO qualificationDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Institution institution = application.getInstitution();

        ApplicationQualification qualification;
        if (qualificationId != null) {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        } else {
            qualification = new ApplicationQualification();
            application.getQualifications().add(qualification);
        }

        ImportedInstitution importedInstitution = importedEntityService.getById(ImportedInstitution.class, institution, qualificationDTO.getInstitution()
                .getId());
        QualificationType qualificationType = importedEntityService.getById(QualificationType.class, institution, qualificationDTO.getType());
        Document qualificationDocument = entityService.getById(Document.class, qualificationDTO.getDocument().getId());
        qualification.setInstitution(importedInstitution);
        qualification.setType(qualificationType);
        qualification.setTitle(Strings.emptyToNull(qualificationDTO.getTitle()));
        qualification.setSubject(qualificationDTO.getSubject());
        qualification.setLanguage(qualificationDTO.getLanguage());
        qualification.setStartDate(qualificationDTO.getStartDate().toLocalDate());
        qualification.setCompleted(qualificationDTO.getCompleted());
        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setAwardDate(qualificationDTO.getAwardDate().toLocalDate());
        qualification.setDocument(qualificationDocument);
        return qualification;
    }

    public void deleteQualification(Integer applicationId, Integer qualificationId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class,
                ImmutableMap.of("application", application, "id", qualificationId));
        application.getQualifications().remove(qualification);
    }

    public ApplicationEmploymentPosition saveEmploymentPosition(Integer applicationId, Integer employmentPositionId,
            ApplicationEmploymentPositionDTO employmentPositionDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationEmploymentPosition employmentPosition;
        if (employmentPositionId != null) {
            employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                    ImmutableMap.of("application", application, "id", employmentPositionId));
        } else {
            employmentPosition = new ApplicationEmploymentPosition();
            application.getEmploymentPositions().add(employmentPosition);
        }

        employmentPosition.setEmployerName(employmentPositionDTO.getEmployerName());

        AddressDTO employerAddressDTO = employmentPositionDTO.getEmployerAddress();
        Address employerAddress = employmentPosition.getEmployerAddress();
        if (employerAddress == null) {
            employerAddress = new Address();
            employmentPosition.setEmployerAddress(employerAddress);
        }
        copyAddress(application.getInstitution(), employerAddress, employerAddressDTO);

        employmentPosition.setPosition(employmentPositionDTO.getPosition());
        employmentPosition.setRemit(employmentPositionDTO.getRemit());
        employmentPosition.setStartDate(employmentPositionDTO.getStartDate().toLocalDate());
        employmentPosition.setCurrent(employmentPositionDTO.getCurrent());
        employmentPosition.setEndDate(employmentPositionDTO.getEndDate() != null ? employmentPositionDTO.getEndDate().toLocalDate() : null);

        return employmentPosition;
    }

    public void deleteEmploymentPosition(Integer applicationId, Integer employmentPositionId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationEmploymentPosition employmentPosition = entityService.getByProperties(ApplicationEmploymentPosition.class,
                ImmutableMap.of("application", application, "id", employmentPositionId));
        application.getEmploymentPositions().remove(employmentPosition);
    }

    public ApplicationFunding saveFunding(Integer applicationId, Integer fundingId, ApplicationFundingDTO fundingDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationFunding funding;
        if (fundingId != null) {
            funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        } else {
            funding = new ApplicationFunding();
            application.getFundings().add(funding);
        }

        FundingSource fundingSource = importedEntityService.getById(FundingSource.class, application.getInstitution(), fundingDTO.getFundingSource());
        Document fundingDocument = entityService.getById(Document.class, fundingDTO.getDocument().getId());

        funding.setFundingSource(fundingSource);
        funding.setDescription(fundingDTO.getDescription());
        funding.setValue(fundingDTO.getValue());
        funding.setAwardDate(fundingDTO.getAwardDate().toLocalDate());
        funding.setDocument(fundingDocument);

        return funding;
    }

    public void deleteFunding(Integer applicationId, Integer fundingId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationFunding funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        application.getFundings().remove(funding);
    }

    public ApplicationReferee saveReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationReferee referee;
        if (refereeId != null) {
            referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        } else {
            referee = new ApplicationReferee();
            application.getReferees().add(referee);
        }

        UserDTO userDTO = refereeDTO.getUser();
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        referee.setUser(user);

        referee.setJobEmployer(refereeDTO.getJobEmployer());
        referee.setJobTitle(refereeDTO.getJobTitle());

        AddressDTO addressDTO = refereeDTO.getAddress();
        Address address = referee.getAddress();
        if (address == null) {
            address = new Address();
            referee.setAddress(address);
        }
        copyAddress(application.getInstitution(), address, addressDTO);

        referee.setPhoneNumber(refereeDTO.getPhoneNumber());
        referee.setSkype(Strings.emptyToNull(refereeDTO.getSkype()));

        return referee;
    }

    public void deleteReferee(Integer applicationId, Integer refereeId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationReferee referee = entityService.getByProperties(ApplicationReferee.class, ImmutableMap.of("application", application, "id", refereeId));
        application.getReferees().remove(referee);
    }

    public void saveAdditionalInformation(Integer applicationId, ApplicationAdditionalInformationDTO additionalInformationDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new ApplicationAdditionalInformation();
            application.setAdditionalInformation(additionalInformation);
        }

        additionalInformation.setConvictionsText(Strings.emptyToNull(additionalInformationDTO.getConvictionsText()));
    }

    private void copyAddress(Institution institution, Address to, AddressDTO from) {
        Domicile currentAddressDomicile = importedEntityService.getById(Domicile.class, institution, from.getDomicile());
        to.setDomicile(currentAddressDomicile);
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(Strings.emptyToNull(from.getAddressLine2()));
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(Strings.emptyToNull(from.getAddressRegion()));
        to.setAddressCode(Strings.isNullOrEmpty(from.getAddressCode()) ? "Not Provided" : from.getAddressCode());
    }

    private LocalDate getNextMonday(LocalDate dateFrom) {
        return dateFrom.plusDays(8 - dateFrom.getDayOfWeek());
    }
    
}
