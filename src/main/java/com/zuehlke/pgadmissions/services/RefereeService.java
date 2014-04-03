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
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
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
    private RoleDAO roleDAO;

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
        if (application.getStatus().isModifiable()) {
            processRefereeAndGetAsUser(referee);
        }
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

    public void refresh(Referee referee) {
        refereeDAO.refresh(referee);
    }

    public void processRefereesRoles(List<Referee> referees) {
        for (Referee referee : referees) {
            processRefereeAndGetAsUser(referee);
        }
    }

    public RegisteredUser processRefereeAndGetAsUser(Referee referee) {
        RegisteredUser user = userService.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
        if (user == null) {
            createAndSaveNewUserWithRefereeRole(referee);
        } else {
            
        }
        
        if (user.getActivationCode() == null) {
            user.setActivationCode(encryptionUtils.generateUUID());
        }
        user.getRoles().add(roleDAO.getRoleByAuthority(Authority.REFEREE));
        

        if (userExists(user) && !isUserReferee(user)) {
            user.getRoles().add(refereeRole);
            if (user.getActivationCode() == null) {
                
            }
        }
        if (!userExists(user)) {
//            user = 
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

    private RegisteredUser createAndSaveNewUserWithRefereeRole(Referee referee) {
        RegisteredUser user = newRegisteredUser();
        user.setEmail(referee.getEmail());
        user.setFirstName(referee.getFirstname());
        user.setLastName(referee.getLastname());
        user.setUsername(referee.getEmail());
        user.setEnabled(false);
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

    public void declineToActAsRefereeAndSendNotification(Referee referee) {
        referee.setDeclined(true);
        refereeDAO.save(referee);
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
        Referee referee = getRefereeById(refereeId);
        ReferenceComment reference = referee.getReference();
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

    public boolean isRefereeOfApplicationForm(RegisteredUser currentUser, ApplicationForm form) {
        // TODO Auto-generated method stub
        return false;
    }

}