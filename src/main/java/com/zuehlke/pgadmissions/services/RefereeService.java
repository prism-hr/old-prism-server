package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RefereeService {

    private final Logger log = LoggerFactory.getLogger(RefereeService.class);
    private final RefereeDAO refereeDAO;
    private final UserService userService;
    private final RoleDAO roleDAO;
    private final CommentService commentService;
    private final EventFactory eventFactory;
    private final ApplicationFormDAO applicationFormDAO;
    private final EncryptionUtils encryptionUtils;
    private final EncryptionHelper encryptionHelper;
    private final MailSendingService mailService;

    public RefereeService() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public RefereeService(RefereeDAO refereeDAO, EncryptionUtils encryptionUtils,
           UserService userService, RoleDAO roleDAO, CommentService commentService,
            EventFactory eventFactory, ApplicationFormDAO applicationFormDAO, EncryptionHelper encryptionHelper,            MailSendingService mailservice) {
        this.refereeDAO = refereeDAO;
        this.encryptionUtils = encryptionUtils;
        this.userService = userService;
        this.roleDAO = roleDAO;
        this.commentService = commentService;
        this.eventFactory = eventFactory;
        this.applicationFormDAO = applicationFormDAO;
        this.encryptionHelper = encryptionHelper;
		this.mailService = mailservice;
    }

    public Referee getRefereeById(Integer id) {
        return refereeDAO.getRefereeById(id);
    }

    public void save(Referee referee) {
        refereeDAO.save(referee);
    }

    public void refresh(Referee referee) {
        refereeDAO.refresh(referee);
    }

    public List<Referee> getRefereesWhoHaveNotProvidedReference(ApplicationForm form) {
        return refereeDAO.getRefereesWhoDidntProvideReferenceYet(form);
    }

    public void saveReferenceAndSendMailNotifications(Referee referee) {
        addReferenceEventToApplication(referee);
        ApplicationForm form = referee.getApplication();
        mailService.scheduleReferenceSubmitConfirmation(form);
    }

    public RegisteredUser getRefereeIfAlreadyRegistered(Referee referee) {
        return userService.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
    }

    public void processRefereesRoles(List<Referee> referees) {
        for (Referee referee : referees) {
            processRefereeAndGetAsUser(referee);
        }
    }

    public RegisteredUser processRefereeAndGetAsUser(Referee referee) {
        RegisteredUser user = userService.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        if (userExists(user) && !isUserReferee(user)) {
            user.getRoles().add(refereeRole);
        }
        if (!userExists(user)) {
            user = createAndSaveNewUserWithRefereeRole(referee, refereeRole);
        }
        referee.setUser(user);
        save(referee);
        return user;
    }

    private boolean userExists(RegisteredUser user) {
        return user != null;
    }

    private boolean isUserReferee(RegisteredUser user) {
        return user.isInRole(Authority.REFEREE);
    }

    private RegisteredUser createAndSaveNewUserWithRefereeRole(Referee referee, Role refereeRole) {
        RegisteredUser user = newRegisteredUser();
        user.setEmail(referee.getEmail());
        user.setFirstName(referee.getFirstname());
        user.setLastName(referee.getLastname());
        user.setUsername(referee.getEmail());
        user.getRoles().add(refereeRole);
        user.setActivationCode(encryptionUtils.generateUUID());
        user.setEnabled(false);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setDirectToUrl(DirectURLsEnum.ADD_REFERENCE.displayValue() + referee.getApplication().getApplicationNumber());
        userService.save(user);
        return user;
    }

    RegisteredUser newRegisteredUser() {
        return new RegisteredUser();
    }

    public void delete(Referee referee) {
        if (referee.getUser() != null) {
            referee.getUser().getReferees().remove(referee);
        }
        refereeDAO.delete(referee);
    }

    public Referee getRefereeByUserAndApplication(RegisteredUser user, ApplicationForm form) {
        Referee matchedReferee = null;
        List<Referee> referees = user.getReferees();
        for (Referee referee : referees) {
            if (referee.getApplication() != null && referee.getApplication().getId().equals(form.getId())) {
                matchedReferee = referee;
            }
        }
        return matchedReferee;
    }

    public void declineToActAsRefereeAndSendNotification(Referee referee) {
        referee.setDeclined(true);
        refereeDAO.save(referee);
        addReferenceEventToApplication(referee);
        ApplicationForm form = referee.getApplication();
        mailService.scheduleReferenceSubmitConfirmation(form);
    }

    public void selectForSendingToPortico(final ApplicationForm applicationForm, final List<Integer> refereesSendToPortico) {

        for (Referee referee : applicationForm.getReferees()) {
            referee.setSendToUCL(false);
        }

        for (Integer refereeId : refereesSendToPortico) {
            Referee referee = refereeDAO.getRefereeById(refereeId);
            referee.setSendToUCL(true);
        }
    }
    
    public ReferenceComment editReferenceComment(RefereesAdminEditDTO refereesAdminEditDTO){
        Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
        Referee referee = getRefereeById(refereeId);
        ReferenceComment reference = referee.getReference();
        
        reference.setComment(refereesAdminEditDTO.getComment());
        reference.setSuitableForUCL(refereesAdminEditDTO.getSuitableForUCL());
        reference.setSuitableForProgramme(refereesAdminEditDTO.getSuitableForProgramme());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            reference.setDocuments(Collections.singletonList(document));
        }
        
        return reference;
    }

    public ReferenceComment postCommentOnBehalfOfReferee(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        Referee referee;
        if (BooleanUtils.isTrue(refereesAdminEditDTO.getContainsRefereeData())) {
            referee = createReferee(refereesAdminEditDTO, applicationForm);
        } else {
            Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
            referee = getRefereeById(refereeId);
        }

        if (referee.getUser() == null) {
            processRefereesRoles(Arrays.asList(referee));
        }

        ReferenceComment referenceComment = createReferenceComment(refereesAdminEditDTO, referee, applicationForm);

        commentService.save(referenceComment);
        
        if (applicationForm.getReferencesToSendToPortico().size() < 2) {
            referee.setSendToUCL(true);
        }

        saveReferenceAndSendMailNotifications(referee);
        return referenceComment;
    }

    private Referee createReferee(RefereesAdminEditDTO refereesAdminEditDTO, ApplicationForm applicationForm) {
        Referee referee = new Referee();
        referee.setApplication(applicationForm);
        referee.setFirstname(refereesAdminEditDTO.getFirstname());
        referee.setLastname(refereesAdminEditDTO.getLastname());
        referee.setAddressLocation(refereesAdminEditDTO.getAddressLocation());
        referee.setJobEmployer(refereesAdminEditDTO.getJobEmployer());
        referee.setJobTitle(refereesAdminEditDTO.getJobTitle());
        referee.setEmail(refereesAdminEditDTO.getEmail());
        referee.setPhoneNumber(refereesAdminEditDTO.getPhoneNumber());
        referee.setMessenger(refereesAdminEditDTO.getMessenger());
        return referee;
    }

    private ReferenceComment createReferenceComment(RefereesAdminEditDTO refereesAdminEditDTO, Referee referee, ApplicationForm applicationForm) {
        ReferenceComment referenceComment = new ReferenceComment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setReferee(referee);
        referenceComment.setType(CommentType.REFERENCE);
        referenceComment.setUser(referee.getUser());
        referenceComment.setProvidedBy(userService.getCurrentUser());
        referenceComment.setComment(refereesAdminEditDTO.getComment());
        referenceComment.setSuitableForProgramme(refereesAdminEditDTO.getSuitableForProgramme());
        referenceComment.setSuitableForUCL(refereesAdminEditDTO.getSuitableForUCL());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            referenceComment.setDocuments(Collections.singletonList(document));
        }
        return referenceComment;
    }

    private void addReferenceEventToApplication(Referee referee) {
        ApplicationForm application = referee.getApplication();
        application.getEvents().add(eventFactory.createEvent(referee));
        applicationFormDAO.save(application);
    }

    public void sendRefereeMailNotification(Referee referee) {
        sendMailToReferee(referee);
    }

    private void sendMailToReferee(Referee referee) {
        try {
            ApplicationForm form = referee.getApplication();
            mailService.sendReferenceRequest(referee, form);
        } catch (Exception e) {
            log.warn("error while sending email", e);
        }
    }
}
