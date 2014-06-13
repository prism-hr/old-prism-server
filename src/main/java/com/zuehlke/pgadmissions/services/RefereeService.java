package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
@Transactional
public class RefereeService {
    
    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private ApplicationService applicationFormService;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

    public ApplicationReferee getById(Integer id) {
        return entityDAO.getById(ApplicationReferee.class, id);
    }

    public void saveOrUpdate(int applicationId, Integer refereeId, ApplicationReferee referee) {
        Application application = applicationDAO.getById(applicationId);
        ApplicationReferee persistentReferee;
        if (refereeId == null) {
            persistentReferee = new ApplicationReferee();
            persistentReferee.setApplication(application);
            application.getReferees().add(persistentReferee);
            applicationFormService.save(application);
        } else {
            persistentReferee = entityDAO.getById(ApplicationReferee.class, refereeId);
        }
        applicationFormCopyHelper.copyReferee(persistentReferee, referee, false);
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

    public void delete(int refereeId) {
        ApplicationReferee referee = entityDAO.getById(ApplicationReferee.class, refereeId);
        referee.getApplication().getReferees().remove(referee);
    }

    public void declineToActAsRefereeAndSendNotification(int refereeId) {
        // TODO post comment instead
//        Referee referee = refereeDAO.getRefereeById(refereeId);
//        referee.setDeclined(true);
    }

    public void selectForSendingToPortico(final Application applicationForm, final List<Integer> refereesSendToPortico) {
        for (ApplicationReferee referee : applicationForm.getReferees()) {
            referee.setIncludeInExport(false);
        }

        for (Integer refereeId : refereesSendToPortico) {
            ApplicationReferee referee = entityDAO.getById(ApplicationReferee.class, refereeId);
            referee.setIncludeInExport(true);
        }
    }

    public Comment editReferenceComment(Application applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        ApplicationReferee referee = entityDAO.getById(ApplicationReferee.class, refereesAdminEditDTO.getEditedRefereeId());
        Comment reference = referee.getComment();
        reference.setContent(refereesAdminEditDTO.getComment());
        reference.setSuitableForInstitution(refereesAdminEditDTO.getSuitableForUCL());
        reference.setSuitableForOpportunity(refereesAdminEditDTO.getSuitableForProgramme());
        reference.setRating(refereesAdminEditDTO.getApplicantRating());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            reference.setDocument(document);
        }

        return reference;
    }

    public Comment postCommentOnBehalfOfReferee(Application applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) {
        ApplicationReferee referee;
        if (BooleanUtils.isTrue(refereesAdminEditDTO.getContainsRefereeData())) {
            referee = createReferee(refereesAdminEditDTO, applicationForm);
        } else {
            referee = entityDAO.getById(ApplicationReferee.class, refereesAdminEditDTO.getEditedRefereeId());
        }

        // TODO make sure uses exists
//        if (referee.getUser() == null) {
//            processRefereesRoles(Arrays.asList(referee));
//        }

// TODO Integrate with workflow
//        Comment referenceComment = createReferenceComment(refereesAdminEditDTO, referee, applicationForm);
//        applicationForm.getApplicationComments().add(referenceComment);
//        commentService.save(referenceComment);

        // FIXME try to notify PorticoService that reference comment has been posted
        // if ( applicationForm.getReferencesToSendToPortico().size() < 2) {
        // referee.setSendToUCL(true);
        // }

        // FIXME call mail sending service
        // saveReferenceAndSendMailNotifications(referee);
        return null;
    }

    private ApplicationReferee createReferee(RefereesAdminEditDTO refereesAdminEditDTO, Application applicationForm) {
        User user = userService.getOrCreateUser(refereesAdminEditDTO.getFirstname(), refereesAdminEditDTO.getLastname(),
                refereesAdminEditDTO.getEmail());

        ApplicationReferee referee = new ApplicationReferee();
        referee.setApplication(applicationForm);
        referee.setUser(user);
        referee.setAddress(refereesAdminEditDTO.getAddressLocation());
        referee.setJobEmployer(refereesAdminEditDTO.getJobEmployer());
        referee.setJobTitle(refereesAdminEditDTO.getJobTitle());
        referee.setPhoneNumber(refereesAdminEditDTO.getPhoneNumber());
        referee.setSkype(refereesAdminEditDTO.getMessenger());
        entityDAO.save(referee);
        return referee;
    }

    private Comment createReferenceComment(RefereesAdminEditDTO refereesAdminEditDTO, ApplicationReferee referee, Application applicationForm) {
        Comment referenceComment = new Comment();
        referenceComment.setApplication(applicationForm);
        referenceComment.setUser(referee.getUser());
        referenceComment.setDelegateUser(userService.getCurrentUser());
        referenceComment.setContent(refereesAdminEditDTO.getComment());
        referenceComment.setSuitableForOpportunity(refereesAdminEditDTO.getSuitableForProgramme());
        referenceComment.setSuitableForInstitution(refereesAdminEditDTO.getSuitableForUCL());
        referenceComment.setRating(refereesAdminEditDTO.getApplicantRating());

        Document document = refereesAdminEditDTO.getReferenceDocument();
        if (document != null) {
            referenceComment.setDocument(document);
        }
        return referenceComment;
    }

}