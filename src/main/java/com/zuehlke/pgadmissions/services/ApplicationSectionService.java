package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_UPDATED_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.Document;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.imported.Country;
import com.zuehlke.pgadmissions.domain.imported.Disability;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.Ethnicity;
import com.zuehlke.pgadmissions.domain.imported.FundingSource;
import com.zuehlke.pgadmissions.domain.imported.Gender;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.Language;
import com.zuehlke.pgadmissions.domain.imported.QualificationType;
import com.zuehlke.pgadmissions.domain.imported.ReferralSource;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.imported.Title;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.application.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationFundingDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationLanguageQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPassportDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationPersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationSupervisorDTO;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ApplicationSectionService {

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    // TODO: validate selection of themes
    public void saveProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws DeduplicationException {
        User userCurrent = userService.getCurrentUser();
        Application application = entityService.getById(Application.class, applicationId);
        Action action = validateUpdateAction(application, userCurrent);

        Institution institution = application.getInstitution();
        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
            application.setProgramDetail(programDetail);
        } else {
            executeUpdateAction(application, action, userCurrent, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
        }

        StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, institution, programDetailDTO.getStudyOption().name());
        ReferralSource referralSource = importedEntityService.getById(ReferralSource.class, institution, programDetailDTO.getReferralSource());
        programDetail.setStudyOption(studyOption);
        programDetail.setStartDate(programDetailDTO.getStartDate());
        programDetail.setReferralSource(referralSource);

        Iterator<ApplicationSupervisor> supervisorsIterator = application.getSupervisors().iterator();
        while (supervisorsIterator.hasNext()) {
            final ApplicationSupervisor supervisor = supervisorsIterator.next();
            Optional<ApplicationSupervisorDTO> supervisorDTO = Iterables.tryFind(programDetailDTO.getSupervisors(), new Predicate<ApplicationSupervisorDTO>() {
                @Override
                public boolean apply(ApplicationSupervisorDTO dto) {
                    return supervisor.getUser().getEmail().equals(dto.getUser().getEmail());
                }
            });
            if (supervisorDTO.isPresent()) {
                programDetailDTO.getSupervisors().remove(supervisorDTO.get());
                supervisor.setAcceptedSupervision(supervisorDTO.get().getAcceptedSupervision());
            } else {
                supervisorsIterator.remove();
            }
        }

        List<String> themes = programDetailDTO.getThemes();
        application.setTheme(themes.isEmpty() ? null : Joiner.on("|").join(themes));

        for (ApplicationSupervisorDTO supervisorDTO : programDetailDTO.getSupervisors()) {
            UserDTO userDTO = supervisorDTO.getUser();
            User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
            ApplicationSupervisor supervisor = new ApplicationSupervisor().withAware(supervisorDTO.getAcceptedSupervision()).withUser(user);
            application.getSupervisors().add(supervisor);
        }
    }

    public ApplicationSupervisor saveSupervisor(Integer applicationId, Integer supervisorId, ApplicationSupervisorDTO supervisorDTO)
            throws DeduplicationException {
        User userCurrent = userService.getCurrentUser();
        Application application = entityService.getById(Application.class, applicationId);
        Action action = validateUpdateAction(application, userCurrent);

        ApplicationSupervisor supervisor;
        if (supervisorId != null) {
            supervisor = entityService.getByProperties(ApplicationSupervisor.class, ImmutableMap.of("application", application, "id", supervisorId));
        } else {
            supervisor = new ApplicationSupervisor();
            application.getSupervisors().add(supervisor);
            executeUpdateAction(application, action, userCurrent, PrismDisplayProperty.APPLICATION_COMMENT_UPDATED_SUPERVISOR);
        }

        UserDTO userDTO = supervisorDTO.getUser();
        User user = userService.getOrCreateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        supervisor.setUser(user);

        supervisor.setAcceptedSupervision(supervisorDTO.getAcceptedSupervision());
        return supervisor;
    }

    public void deleteSupervisor(Integer applicationId, Integer supervisorId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationSupervisor supervisor = entityService.getByProperties(ApplicationSupervisor.class,
                ImmutableMap.of("application", application, "id", supervisorId));
        application.getSupervisors().remove(supervisor);
    }

    public void savePersonalDetail(Integer applicationId, ApplicationPersonalDetailDTO personalDetailDTO) throws DeduplicationException {
        User userCurrent = userService.getCurrentUser();
        Application application = entityService.getById(Application.class, applicationId);
        Action action = validateUpdateAction(application, userCurrent);

        Institution institution = application.getInstitution();
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        if (personalDetail == null) {
            personalDetail = new ApplicationPersonalDetail();
            application.setPersonalDetail(personalDetail);
        } else {
            executeUpdateAction(application, action, userCurrent, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL);
        }

        User userCreator = application.getUser();
        if (userCurrent.getId().equals(userCreator.getId())) {
            userCreator.setFirstName(personalDetailDTO.getUser().getFirstName());
            userCreator.setFirstName2(Strings.emptyToNull(personalDetailDTO.getUser().getFirstName2()));
            userCreator.setFirstName3(Strings.emptyToNull(personalDetailDTO.getUser().getFirstName3()));
            userCreator.setLastName(personalDetailDTO.getUser().getLastName());
        }

        Title title = importedEntityService.getById(Title.class, institution, personalDetailDTO.getTitle());
        Gender gender = importedEntityService.getById(Gender.class, institution, personalDetailDTO.getGender());
        Country country = importedEntityService.getById(Country.class, institution, personalDetailDTO.getCountry());
        Language firstNationality = importedEntityService.getById(Language.class, institution, personalDetailDTO.getFirstNationality());
        Language secondNationality = personalDetailDTO.getSecondNationality() != null ? importedEntityService.<Language> getById(Language.class, institution,
                personalDetailDTO.getSecondNationality()) : null;
        Domicile residenceCountry = importedEntityService.getById(Domicile.class, institution, personalDetailDTO.getDomicile());
        Ethnicity ethnicity = importedEntityService.getById(Ethnicity.class, institution, personalDetailDTO.getEthnicity());
        Disability disability = importedEntityService.getById(Disability.class, institution, personalDetailDTO.getDisability());
        personalDetail.setTitle(title);
        personalDetail.setGender(gender);
        personalDetail.setDateOfBirth(personalDetailDTO.getDateOfBirth());
        personalDetail.setCountry(country);
        personalDetail.setFirstNationality(firstNationality);
        personalDetail.setSecondNationality(secondNationality);
        personalDetail.setFirstLanguageLocale(personalDetailDTO.getFirstLanguageLocale());
        personalDetail.setDomicile(residenceCountry);
        personalDetail.setVisaRequired(personalDetailDTO.getVisaRequired());
        personalDetail.setPhone(personalDetailDTO.getPhone());
        personalDetail.setSkype(Strings.emptyToNull(personalDetailDTO.getSkype()));
        personalDetail.setEthnicity(ethnicity);
        personalDetail.setDisability(disability);

        ApplicationLanguageQualificationDTO languageQualificationDTO = personalDetailDTO.getLanguageQualification();
        if (languageQualificationDTO == null) {
            personalDetail.setLanguageQualification(null);
        } else {
            ApplicationLanguageQualification languageQualification = personalDetail.getLanguageQualification();
            if (languageQualification == null) {
                languageQualification = new ApplicationLanguageQualification();
                personalDetail.setLanguageQualification(languageQualification);
            }
            ImportedLanguageQualificationType languageQualificationType = importedEntityService.getById(ImportedLanguageQualificationType.class, institution,
                    languageQualificationDTO.getType());
            Document proofOfAward = entityService.getById(Document.class, languageQualificationDTO.getProofOfAward().getId());
            languageQualification.setType(languageQualificationType);
            languageQualification.setExamDate(languageQualificationDTO.getExamDate());
            languageQualification.setOverallScore(languageQualificationDTO.getOverallScore());
            languageQualification.setReadingScore(languageQualificationDTO.getReadingScore());
            languageQualification.setWritingScore(languageQualificationDTO.getWritingScore());
            languageQualification.setSpeakingScore(languageQualificationDTO.getSpeakingScore());
            languageQualification.setListeningScore(languageQualificationDTO.getListeningScore());
            languageQualification.setDocument(proofOfAward);
        }

        ApplicationPassportDTO passportDTO = personalDetailDTO.getPassport();
        if (passportDTO == null) {
            personalDetail.setPassport(null);
        } else {
            ApplicationPassport passport = personalDetail.getPassport();
            if (passport == null) {
                passport = new ApplicationPassport();
                personalDetail.setPassport(passport);
            }
            passport.setNumber(passportDTO.getNumber());
            passport.setName(passportDTO.getName());
            passport.setIssueDate(passportDTO.getIssueDate());
            passport.setExpiryDate(passportDTO.getExpiryDate());
        }
    }

    public void saveAddress(Integer applicationId, ApplicationAddressDTO addressDTO) throws DeduplicationException {
        User userCurrent = userService.getCurrentUser();
        Application application = entityService.getById(Application.class, applicationId);
        Action action = validateUpdateAction(application, userCurrent);

        Institution institution = application.getInstitution();

        ApplicationAddress address = application.getAddress();
        if (address == null) {
            address = new ApplicationAddress();
            application.setAddress(address);
        } else {
            executeUpdateAction(application, action, userCurrent, APPLICATION_COMMENT_UPDATED_ADDRESS);
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
        qualification.setStartDate(qualificationDTO.getStartDate());
        qualification.setCompleted(BooleanUtils.isTrue(qualificationDTO.getCompleted()));
        qualification.setGrade(qualificationDTO.getGrade());
        qualification.setAwardDate(qualificationDTO.getAwardDate());
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
        employmentPosition.setStartDate(employmentPositionDTO.getStartDate());
        employmentPosition.setCurrent(BooleanUtils.isTrue(employmentPositionDTO.getCurrent()));
        employmentPosition.setEndDate(employmentPositionDTO.getEndDate());

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
        funding.setAwardDate(fundingDTO.getAwardDate());
        funding.setDocument(fundingDocument);

        return funding;
    }

    public void deleteFunding(Integer applicationId, Integer fundingId) {
        Application application = entityService.getById(Application.class, applicationId);
        ApplicationFunding funding = entityService.getByProperties(ApplicationFunding.class, ImmutableMap.of("application", application, "id", fundingId));
        application.getFundings().remove(funding);
    }

    public ApplicationReferee saveReferee(Integer applicationId, Integer refereeId, ApplicationRefereeDTO refereeDTO) throws DeduplicationException {
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

        referee.setPhone(refereeDTO.getPhone());
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
        to.setAddressCode(Strings.emptyToNull(from.getAddressCode()));
    }

    private Action validateUpdateAction(Application application, User user) {
        Action action = actionService.getViewEditAction(application);
        if (!actionService.checkActionAvailable(application, action, user)) {
            actionService.throwWorkflowPermissionException(application, action);
        }
        return action;
    }

    private void executeUpdateAction(Application application, Action action, User invoker, PrismDisplayProperty messageIndex) throws DeduplicationException {
        Comment comment = new Comment().withUser(invoker).withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).withResource(application).load(messageIndex))
                .withCreatedTimestamp(new DateTime());
        actionService.executeUserAction(application, action, comment);
    }

}
