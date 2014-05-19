package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RefereeService {
    // TODO fix tests

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public Referee getRefereeById(Integer id) {
        return refereeDAO.getRefereeById(id);
    }

    public Referee getOrCreate(Integer refereeId) {
        if (refereeId == null) {
            return new Referee();
        }
        return getSecuredInstance(refereeId);
    }

    public void saveOrUpdate(ApplicationForm application, Integer refereeId, Referee referee) {
        Referee persistentReferee;
        if (refereeId == null) {
            persistentReferee = new Referee();
            persistentReferee.setApplication(application);
            application.getReferees().add(persistentReferee);
            applicationFormService.save(application);
        } else {
            persistentReferee = getSecuredInstance(refereeId);
        }
        applicationFormCopyHelper.copyReferee(persistentReferee, referee, false);
        applicationFormService.saveOrUpdateApplicationSection(application);
        // FIXME check if can edit referees
        // if (application.getState().isModifiable()) {
        processRefereeAndGetAsUser(referee);
        // }
    }

    // TODO finish it
    // ApplicationForm application = newReferee.getApplication();
    //
    // if (referee == null) {
    // referee = newReferee;
    // } else {
    // referee.setFirstname(newReferee.getFirstname());
    // referee.setLastname(newReferee.getLastname());
    // if (referee.getEmail() != newReferee.getEmail()) {
    // delete(referee);
    // }
    // referee.setEmail(newReferee.getEmail());
    // referee.setJobEmployer(newReferee.getJobEmployer());
    // referee.setJobTitle(newReferee.getJobTitle());
    // referee.setAddressLocation(newReferee.getAddressLocation());
    // referee.setPhoneNumber(newReferee.getPhoneNumber());
    // referee.setMessenger(newReferee.getMessenger());
    // }
    //
    // if (!application.getStatus().isSubmitted()) {
    // save(referee);
    // } else if (application.getStatus().isModifiable()) {
    //
    // }
    //
    // }

    public void processRefereesRoles(List<Referee> referees) {
        for (Referee referee : referees) {
            processRefereeAndGetAsUser(referee);
        }
    }

    public User processRefereeAndGetAsUser(Referee referee) {
        User user = referee.getUser();
        if (user.getActivationCode() == null) {
            user.setActivationCode(encryptionUtils.generateUUID());
        }

        if (!roleService.hasRole(user, Authority.APPLICATION_REFEREE, referee.getApplication())) {
            // user.getRoles().add(refereeRole);
            if (user.getActivationCode() == null) {

            }
        }
        referee.setUser(user);
        applicationFormUserRoleService.createRefereeRole(referee);
        return user;
    }

    public void delete(Referee referee) {
        User refereeUser = referee.getUser();
        if (refereeUser != null) {
            referee.getUser().getReferees().remove(referee);
            applicationFormUserRoleService.deleteApplicationRole(referee.getApplication(), refereeUser, Authority.APPLICATION_REFEREE);
        }
        refereeDAO.delete(referee);
    }

    public void declineToActAsRefereeAndSendNotification(int refereeId) {
        Referee referee = refereeDAO.getRefereeById(refereeId);
        referee.setDeclined(true);
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

    public Comment editReferenceComment(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
        Referee referee = getRefereeById(refereeId);
        Comment reference = referee.getComment();
        reference.setContent(refereesAdminEditDTO.getComment());
        reference.setSuitableForInstitution(refereesAdminEditDTO.getSuitableForUCL());
        reference.setSuitableForProgramme(refereesAdminEditDTO.getSuitableForProgramme());
        reference.setRating(refereesAdminEditDTO.getApplicantRating());
        reference.getScores().clear();
        reference.getScores().addAll(refereesAdminEditDTO.getScores());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            reference.setDocument(document);
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
        applicationForm.getApplicationComments().add(referenceComment);
        commentService.save(referenceComment);

        // FIXME try to notify PorticoService that reference comment has been posted
        // if ( applicationForm.getReferencesToSendToPortico().size() < 2) {
        // referee.setSendToUCL(true);
        // }
        applicationFormUserRoleService.referencePosted(referenceComment);

        // FIXME call mail sending service
        // saveReferenceAndSendMailNotifications(referee);
        return referenceComment;
    }

    private Referee createReferee(RefereesAdminEditDTO refereesAdminEditDTO, ApplicationForm applicationForm) {
        User user = userService.getOrCreateUser(refereesAdminEditDTO.getFirstname(), refereesAdminEditDTO.getLastname(),
                refereesAdminEditDTO.getEditedRefereeId());

        Referee referee = new Referee();
        referee.setApplication(applicationForm);
        referee.setUser(user);
        referee.setAddress(refereesAdminEditDTO.getAddressLocation());
        referee.setJobEmployer(refereesAdminEditDTO.getJobEmployer());
        referee.setJobTitle(refereesAdminEditDTO.getJobTitle());
        referee.setPhoneNumber(refereesAdminEditDTO.getPhoneNumber());
        referee.setMessenger(refereesAdminEditDTO.getMessenger());
        refereeDAO.save(referee);
        return referee;
    }

    private ReferenceComment createReferenceComment(RefereesAdminEditDTO refereesAdminEditDTO, Referee referee, ApplicationForm applicationForm) {
        ReferenceComment referenceComment = new ReferenceComment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setUser(referee.getUser());
        referenceComment.setDelegateProvider(userService.getCurrentUser());
        referenceComment.setContent(refereesAdminEditDTO.getComment());
        referenceComment.setSuitableForProgramme(refereesAdminEditDTO.getSuitableForProgramme());
        referenceComment.setSuitableForInstitution(refereesAdminEditDTO.getSuitableForUCL());
        referenceComment.setRating(refereesAdminEditDTO.getApplicantRating());
        referenceComment.getScores().addAll(refereesAdminEditDTO.getScores());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            referenceComment.setDocument(document);
        }
        return referenceComment;
    }

    private Referee getSecuredInstance(Integer refereeId) {
        Referee referee = getRefereeById(refereeId);
        if (referee == null) {
            throw new ResourceNotFoundException();
        }
        return referee;
    }

    public boolean isRefereeOfApplicationForm(User currentUser, ApplicationForm form) {
        // TODO Auto-generated method stub
        return false;
    }

}