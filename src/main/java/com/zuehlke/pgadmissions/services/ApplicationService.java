package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.Title;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.ReportFormat;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationLanguageQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPassportDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;

@Service
@Transactional
public class ApplicationService {

    public static final int APPLICATION_BLOCK_SIZE = 50;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationFormListDAO applicationFormListDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ProgramDetailsService programDetailsService;

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

    public List<Application> getApplicationsForList(final User user, final Filter filtering) {
        Filter userFilter = user.getUserAccount().getFilters().get(PrismScope.APPLICATION);
        if (userFilter.getPage() == 1) {
            userFilter.setLastAccessTimestamp(new DateTime());
        }

        List<Application> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        return applications;
    }

    public List<Application> getApplicationsForReport(final User user, final Filter filtering, final ReportFormat reportType) {
        return applicationFormListDAO.getVisibleApplicationsForReport(user, filtering);
    }

    public List<Application> getApplicationsByStatus(final PrismState status) {
        return applicationDAO.getAllApplicationsByStatus(status);
    }

    public List<Application> getApplicationsForProject(final Project project) {
        return applicationDAO.getApplicationsByProject(project);
    }

    public void saveOrUpdateApplicationSection(Application application) {
    }

    public Date getDefaultStartDateForApplication(Application application) {
        Program program = application.getProgram();
        StudyOption studyOption = application.getProgramDetails().getStudyOption();
        if (program != null && studyOption != null) {
            return programService.getDefaultStartDate(program, studyOption);
        }
        return null;
    }

    public Application getApplicationDescriptorForUser(final Application application, final User user) {
        Application applicationDescriptor = new Application();
//        applicationDescriptor.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getId()));
//        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormDAO.getRaisesUrgentFlagForUser(application, user));
//        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormDAO.getRaisesUpdateFlagForUser(application, user));
        return applicationDescriptor;
    }

    public List<ResourceConsoleListRowDTO> getConsoleListBlock(Integer page, Integer perPage) {
        return resourceService.getConsoleListBlock(Application.class, page, perPage);
    }

    public void saveProgramDetails(Integer applicationId, ApplicationProgramDetailsDTO programDetailsDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationProgramDetails programDetails = application.getProgramDetails();

        StudyOption studyOption = entityService.getById(StudyOption.class, programDetailsDTO.getStudyOption());
        ReferralSource referralSource = entityService.getById(ReferralSource.class, programDetailsDTO.getReferralSource());
        programDetails.setStudyOption(studyOption);
        programDetails.setStartDate(programDetailsDTO.getStartDate().toLocalDate());
        programDetails.setReferralSource(referralSource);

        // TODO replace supervisors
//        programDetails.getSupervisors().clear();
        for (ApplicationSupervisorDTO supervisorDTO : programDetailsDTO.getSupervisors()) {
        }
    }

    public void savePersonalDetails(Integer applicationId, ApplicationPersonalDetailsDTO personalDetailsDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new ApplicationPersonalDetails();
            application.setPersonalDetails(personalDetails);
        }

        User user = application.getUser();
        user.setFirstName(personalDetailsDTO.getUser().getFirstName());
        user.setFirstName2(personalDetailsDTO.getUser().getFirstName2());
        user.setFirstName3(personalDetailsDTO.getUser().getFirstName3());
        user.setLastName(personalDetailsDTO.getUser().getLastName());

        Title title = entityService.getById(Title.class, personalDetailsDTO.getTitle());
        Country country = entityService.getById(Country.class, personalDetailsDTO.getCountry());
        Language firstNationality = entityService.getById(Language.class, personalDetailsDTO.getFirstNationality());
        Language secondNationality = personalDetailsDTO.getSecondNationality() != null ? entityService.getById(Language.class, personalDetailsDTO.getSecondNationality()) : null;
        Domicile residenceCountry = entityService.getById(Domicile.class, personalDetailsDTO.getResidenceCountry());
        Ethnicity ethnicity = entityService.getById(Ethnicity.class, personalDetailsDTO.getEthnicity());
        Disability disability = entityService.getById(Disability.class, personalDetailsDTO.getDisability());
        personalDetails.setTitle(title);
        personalDetails.setGender(personalDetailsDTO.getGender());
        personalDetails.setDateOfBirth(personalDetailsDTO.getDateOfBirth().toLocalDate());
        personalDetails.setCountry(country);
        personalDetails.setFirstNationality(firstNationality);
        personalDetails.setSecondNationality(secondNationality);
        personalDetails.setFirstLanguageEnglish(personalDetailsDTO.getFirstLanguageEnglish());
        personalDetails.setResidenceCountry(residenceCountry);
        personalDetails.setVisaRequired(personalDetailsDTO.getVisaRequired());
        personalDetails.setPhoneNumber(personalDetailsDTO.getPhoneNumber());
        personalDetails.setMessenger(personalDetailsDTO.getMessenger());
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
            LanguageQualificationType languageQualificationType = entityService.getById(LanguageQualificationType.class, languageQualificationDTO.getType());
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
        copyAddress(currentAddress, currentAddressDTO);

        AddressDTO contactAddressDTO = addressDTO.getContactAddress();
        Address contactAddress = address.getContactAddress();
        if (contactAddress == null) {
            contactAddress = new Address();
            address.setContactAddress(contactAddress);
        }
        copyAddress(contactAddress, contactAddressDTO);
    }

    public ApplicationQualification saveQualification(Integer applicationId, Integer qualificationId, ApplicationQualificationDTO qualificationDTO) {
        Application application = entityService.getById(Application.class, applicationId);

        ApplicationQualification qualification;
        if (qualificationId != null) {
            qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        } else {
            qualification = new ApplicationQualification();
            application.getQualifications().add(qualification);
        }

        ImportedInstitution institution = entityService.getById(ImportedInstitution.class, qualificationDTO.getInstitution().getId());
        QualificationType qualificationType = entityService.getById(QualificationType.class, qualificationDTO.getType());
        Document qualificationDocument = entityService.getById(Document.class, qualificationDTO.getDocument().getId());
        qualification.setInstitution(institution);
        qualification.setType(qualificationType);
        qualification.setTitle(qualificationDTO.getTitle());
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
        ApplicationQualification qualification = entityService.getByProperties(ApplicationQualification.class, ImmutableMap.of("application", application, "id", qualificationId));
        application.getQualifications().remove(qualification);
    }

    private void copyAddress(Address to, AddressDTO from) {
        Domicile currentAddressDomicile = entityService.getById(Domicile.class, from.getDomicile());
        to.setDomicile(currentAddressDomicile);
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(from.getAddressLine2());
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(from.getAddressRegion());
        to.setAddressCode(from.getAddressCode());
    }

}
