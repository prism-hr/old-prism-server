package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RefereeService {

    private final RefereeDAO refereeDAO;
    private final UserService userService;
    private final RoleDAO roleDAO;
    private final CommentService commentService;
    private final ApplicationFormDAO applicationFormDAO;
    private final EncryptionUtils encryptionUtils;
    private final EncryptionHelper encryptionHelper;
    private final ApplicantRatingService applicantRatingService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public RefereeService() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public RefereeService(RefereeDAO refereeDAO, EncryptionUtils encryptionUtils, UserService userService, RoleDAO roleDAO, CommentService commentService,
            ApplicationFormDAO applicationFormDAO, EncryptionHelper encryptionHelper, ApplicantRatingService applicantRatingService,
            ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.refereeDAO = refereeDAO;
        this.encryptionUtils = encryptionUtils;
        this.userService = userService;
        this.roleDAO = roleDAO;
        this.commentService = commentService;
        this.applicationFormDAO = applicationFormDAO;
        this.encryptionHelper = encryptionHelper;
        this.applicantRatingService = applicantRatingService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    public Referee getRefereeById(Integer id) {
        return refereeDAO.getRefereeById(id);
    }
    
    public Referee getRefereeById(String idString) {
        Integer id = encryptionHelper.decryptToInteger(idString);
        return refereeDAO.getRefereeById(id);
    }

    public void save(Referee referee) {
        refereeDAO.save(referee);
    }

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

        applicantRatingService.computeAverageRating(applicationForm);

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
        applicantRatingService.computeAverageRating(applicationForm);

        if (applicationForm.getReferencesToSendToPortico().size() < 2) {
            referee.setSendToUCL(true);
        }

        applicationFormUserRoleService.referencePosted(referee);

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

}