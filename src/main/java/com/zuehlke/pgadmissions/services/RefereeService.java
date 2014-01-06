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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
@Transactional
public class RefereeService {

    private final RefereeDAO refereeDAO;
    private final CommentService commentService;
    private final EventFactory eventFactory;
    private final ApplicationFormDAO applicationFormDAO;
    private final EncryptionHelper encryptionHelper;
    private final ApplicantRatingService applicantRatingService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public RefereeService() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public RefereeService(RefereeDAO refereeDAO, CommentService commentService, EventFactory eventFactory, ApplicationFormDAO applicationFormDAO, 
    		EncryptionHelper encryptionHelper, ApplicantRatingService applicantRatingService, ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.refereeDAO = refereeDAO;
        this.commentService = commentService;
        this.eventFactory = eventFactory;
        this.applicationFormDAO = applicationFormDAO;
        this.encryptionHelper = encryptionHelper;
        this.applicantRatingService = applicantRatingService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
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

    public void processRefereesRoles(List<Referee> referees) {
        for (Referee referee : referees) {
            RegisteredUser user = applicationFormUserRoleService.createRegisteredUser(referee.getFirstname(), referee.getLastname(), referee.getEmail());
            referee.setUser(user);
            save(referee);
        }
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

    public Referee getRefereeForApplication(RegisteredUser user, ApplicationForm application) { 
    	return refereeDAO.getRefereeByUserAndApplication(user, application);
    }
    
    public Referee getRefereeByUser(RegisteredUser user) {
    	return refereeDAO.getRefereeByUser(user);
    }

    public void declineToActAsRefereeAndSendNotification(Referee referee) {
    	ReferenceComment reference = new ReferenceComment();
    	reference.setApplication(referee.getApplication());
    	reference.setDeclined(true);
    	reference.setType(CommentType.REFERENCE);
    	reference.setUser(referee.getUser());
    	commentService.save(reference);
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
        Referee referee = getRefereeById(refereeId);
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
        
        this.addReferenceEventToApplication(referee);
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
        referenceComment.setProvidedBy(applicationFormUserRoleService.getCurrentUser());
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

    public void addReferenceEventToApplication(Referee referee) {
        ApplicationForm application = referee.getApplication();
        application.getEvents().add(eventFactory.createEvent(referee));
        applicationFormDAO.save(application);
        applicationFormUserRoleService.referencePosted(referee);
    }

}