package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
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
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RefereeService {

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private EncryptionHelper encryptionHelper;
    
    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private ApplicantRatingService applicantRatingService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    public Referee getById(Integer id) {
        return refereeDAO.getRefereeById(id);
    }

    public void save(Referee referee) {
        refereeDAO.save(referee);
    }

    /**
     * Why are we doing this instead of hibernate save or update?
     * @param referee
     * @param newReferee
     * @Author Alastair Knowles
     */
    public void saveOrUpdate(Referee referee, Referee newReferee) {
        ApplicationForm application = newReferee.getApplication();
        
        if (referee == null) {
            referee = newReferee;
        } else {
            referee.setFirstname(newReferee.getFirstname());
            referee.setLastname(newReferee.getLastname());
            if (referee.getEmail() != newReferee.getEmail()) {
                delete(referee);
            }
            referee.setEmail(newReferee.getEmail());
            referee.setJobEmployer(newReferee.getJobEmployer());
            referee.setJobTitle(newReferee.getJobTitle());
            referee.setAddressLocation(newReferee.getAddressLocation());
            referee.setPhoneNumber(newReferee.getPhoneNumber());
            referee.setMessenger(newReferee.getMessenger());
        }

        if (!application.getStatus().isSubmitted()) {
            save(referee);
        } else if (application.getStatus().isModifiable()) {
            processRefereeAndGetAsUser(referee);
        }
        
        applicationsService.save(application);
        applicationFormUserRoleService.insertApplicationUpdate(application, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
    }

    public void refresh(Referee referee) {
        refereeDAO.refresh(referee);
    }

    public void saveReferenceAndSendMailNotifications(Referee referee) {
        addReferenceEventToApplication(referee);
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
            if (user.getActivationCode() == null) {
                user.setActivationCode(encryptionUtils.generateUUID());
            }
        }
        if (!userExists(user)) {
            user = createAndSaveNewUserWithRefereeRole(referee, refereeRole);
        }
        referee.setUser(user);
        save(referee);
        applicationFormUserRoleService.createRefereeRole(referee);
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
        RegisteredUser refereeUser = referee.getUser();
        if (refereeUser != null) {
            referee.getUser().getReferees().remove(referee);
            applicationFormUserRoleService.deleteApplicationRole(referee.getApplication(), refereeUser, Authority.REFEREE);
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

    public ReferenceComment editReferenceComment(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
        Referee referee = getById(refereeId);
        ReferenceComment reference = referee.getReference();
        reference.setComment(refereesAdminEditDTO.getComment());
        reference.setSuitableForUCL(refereesAdminEditDTO.getSuitableForUCL());
        reference.setSuitableForProgramme(refereesAdminEditDTO.getSuitableForProgramme());
        reference.setApplicantRating(refereesAdminEditDTO.getApplicantRating());
        reference.getScores().clear();
        reference.getScores().addAll(refereesAdminEditDTO.getScores());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            reference.setDocuments(Collections.singletonList(document));
        }

        applicantRatingService.computeAverageRating(applicationForm);
        return reference;
    }

    public ReferenceComment postCommentOnBehalfOfReferee(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        Referee referee;
        if (BooleanUtils.isTrue(refereesAdminEditDTO.getContainsRefereeData())) {
            referee = createReferee(refereesAdminEditDTO, applicationForm);
        } else {
            Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
            referee = getById(refereeId);
        }

        if (referee.getUser() == null) {
            processRefereesRoles(Arrays.asList(referee));
        }

        ReferenceComment referenceComment = createReferenceComment(refereesAdminEditDTO, referee, applicationForm);
        applicationForm.getApplicationComments().add(referenceComment);
        commentService.save(referenceComment);
        applicantRatingService.computeAverageRating(applicationForm);

        if (applicationForm.getReferencesToSendToPortico().size() < 2) {
            referee.setSendToUCL(true);
        }

        applicationFormUserRoleService.referencePosted(referee);
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
        referenceComment.setApplicantRating(refereesAdminEditDTO.getApplicantRating());
        referenceComment.setScores(refereesAdminEditDTO.getScores());

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

}