package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.APPLICATION_MOVED_TO_APPROVED_NOTIFICATION;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApprovalService {

    private final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    private final ApplicationFormDAO applicationDAO;

    private final ApprovalRoundDAO approvalRoundDAO;

    private final StageDurationService stageDurationService;

    private final EventFactory eventFactory;

    private final CommentDAO commentDAO;

    private final ProgrammeDetailDAO programmeDetailDAO;

    private final UserService userService;

    private final PorticoQueueService approvedSenderService;

    private final SupervisorDAO supervisorDAO;

    private final MailSendingService mailSendingService;

    private final ProgramInstanceService programInstanceService;

    public ApprovalService() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ApprovalService(UserService userService, ApplicationFormDAO applicationDAO, ApprovalRoundDAO approvalRoundDAO,
            StageDurationService stageDurationService, EventFactory eventFactory, CommentDAO commentDAO, SupervisorDAO supervisorDAO,
            ProgrammeDetailDAO programmeDetailDAO, PorticoQueueService approvedSenderService, MailSendingService mailSendingService,
            ProgramInstanceService programInstanceService) {
        this.userService = userService;
        this.applicationDAO = applicationDAO;
        this.approvalRoundDAO = approvalRoundDAO;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.commentDAO = commentDAO;
        this.supervisorDAO = supervisorDAO;
        this.programmeDetailDAO = programmeDetailDAO;
        this.approvedSenderService = approvedSenderService;
        this.mailSendingService = mailSendingService;
        this.programInstanceService = programInstanceService;
    }

    public void confirmOrDeclineSupervision(ApplicationForm form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalRound approvalRound = form.getLatestApprovalRound();
        Supervisor supervisor = approvalRound.getPrimarySupervisor();
        Boolean confirmed = confirmSupervisionDTO.getConfirmedSupervision();

        supervisor.setConfirmedSupervision(confirmed);

        if (BooleanUtils.isTrue(confirmed)) {
            // override secondary supervisor
            Supervisor secondarySupervisor = approvalRound.getSecondarySupervisor();
            if (!secondarySupervisor.getUser().getEmail().equals(confirmSupervisionDTO.getSecondarySupervisorEmail())) {
                RegisteredUser user = userService.getUserByEmail(confirmSupervisionDTO.getSecondarySupervisorEmail());
                approvalRound.getSupervisors().remove(secondarySupervisor); // remove old supervisor
                
                Supervisor newSecondarySupervisor = new Supervisor();
                newSecondarySupervisor.setUser(user);
                newSecondarySupervisor.setApprovalRound(approvalRound);
                approvalRound.getSupervisors().add(newSecondarySupervisor);
            }

            approvalRound.setProjectDescriptionAvailable(true);
            approvalRound.setProjectTitle(confirmSupervisionDTO.getProjectTitle());
            approvalRound.setProjectAbstract(confirmSupervisionDTO.getProjectAbstract());
            approvalRound.setRecommendedConditionsAvailable(confirmSupervisionDTO.getRecommendedConditionsAvailable());
            approvalRound.setRecommendedConditions(confirmSupervisionDTO.getRecommendedConditions());
            approvalRound.setRecommendedStartDate(confirmSupervisionDTO.getRecommendedStartDate());
            approvalRound.setProjectAcceptingApplications(confirmSupervisionDTO.getProjectAcceptingApplications());
            supervisor.setConfirmedSupervisionDate(new Date());
        }

        if (BooleanUtils.isFalse(confirmed)) {
            supervisor.setDeclinedSupervisionReason(confirmSupervisionDTO.getDeclinedSupervisionReason());
            form.setDueDate(new Date());
        }

        SupervisionConfirmationComment supervisionConfirmationComment = createSupervisionConfirmationComment(confirmSupervisionDTO, approvalRound, supervisor);
        commentDAO.save(supervisionConfirmationComment);
    }

    private SupervisionConfirmationComment createSupervisionConfirmationComment(ConfirmSupervisionDTO confirmSupervisionDTO, ApprovalRound approvalRound,
            Supervisor supervisor) {
        SupervisionConfirmationComment supervisionConfirmationComment = new SupervisionConfirmationComment();
        supervisionConfirmationComment.setApplication(approvalRound.getApplication());
        supervisionConfirmationComment.setDate(new Date());
        supervisionConfirmationComment.setSupervisor(supervisor);
        supervisionConfirmationComment.setType(CommentType.SUPERVISION_CONFIRMATION);
        supervisionConfirmationComment.setUser(userService.getCurrentUser());
        supervisionConfirmationComment.setComment(StringUtils.EMPTY);
        supervisionConfirmationComment.setSecondarySupervisor(approvalRound.getSecondarySupervisor());

        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            supervisionConfirmationComment.setProjectTitle(confirmSupervisionDTO.getProjectTitle());
            supervisionConfirmationComment.setProjectAbstract(confirmSupervisionDTO.getProjectAbstract());
            supervisionConfirmationComment.setRecommendedStartDate(confirmSupervisionDTO.getRecommendedStartDate());
            Boolean recommendedConditionsAvailable = confirmSupervisionDTO.getRecommendedConditionsAvailable();
            supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
            if (BooleanUtils.isTrue(recommendedConditionsAvailable)) {
                supervisionConfirmationComment.setRecommendedConditions(confirmSupervisionDTO.getRecommendedConditions());
            } else {
                supervisionConfirmationComment.setRecommendedConditions(null);
            }
        }

        return supervisionConfirmationComment;
    }

    public void moveApplicationToApproval(ApplicationForm form, ApprovalRound approvalRound) {
        checkApplicationStatus(form);
        checkSendToPorticoStatus(form, approvalRound);
        copyLastNotifiedForRepeatSupervisors(form, approvalRound);
        form.setLatestApprovalRound(approvalRound);
        form.addNotificationRecord(new NotificationRecord(NotificationType.APPROVAL_REMINDER));

        approvalRound.setApplication(form);
        approvalRoundDAO.save(approvalRound);

        StageDuration approveStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.APPROVAL);
        DateTime dueDate = DateUtils.addWorkingDaysInMinutes(new DateTime(), approveStageDuration.getDurationInMinutes());
        form.setDueDate(dueDate.toDate());

        form.getEvents().add(eventFactory.createEvent(approvalRound));

        boolean sendReferenceRequest = form.getStatus() == ApplicationFormStatus.VALIDATION;

        form.setStatus(ApplicationFormStatus.APPROVAL);
        resetNotificationRecords(form);

        applicationDAO.save(form);

        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setApplication(form);
        approvalComment.setComment(StringUtils.EMPTY);
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setProjectAbstract(approvalRound.getProjectAbstract());
        approvalComment.setProjectDescriptionAvailable(approvalRound.getProjectDescriptionAvailable());
        approvalComment.setProjectTitle(approvalRound.getProjectTitle());
        approvalComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
        approvalComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        approvalComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
        approvalComment.setSupervisor(approvalRound.getPrimarySupervisor());
        approvalComment.setSecondarySupervisor(approvalRound.getSecondarySupervisor());
        approvalComment.setUser(userService.getCurrentUser());

        if (sendReferenceRequest) {
            mailSendingService.sendReferenceRequest(form.getReferees(), form);
        }
        commentDAO.save(approvalComment);
    }

    private void copyLastNotifiedForRepeatSupervisors(ApplicationForm form, ApprovalRound approvalRound) {
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();
        if (latestApprovalRound != null) {
            List<Supervisor> supervisors = latestApprovalRound.getSupervisors();
            for (Supervisor supervisor : supervisors) {
                List<Supervisor> newSupervisors = approvalRound.getSupervisors();
                for (Supervisor newSupervisor : newSupervisors) {
                    if (supervisor.getUser().getId().equals(newSupervisor.getUser().getId())) {
                        newSupervisor.setLastNotified(supervisor.getLastNotified());
                    }
                }
            }
        }
    }

    private void resetNotificationRecords(ApplicationForm form) {
        form.removeNotificationRecord(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION, NotificationType.APPROVAL_RESTART_REQUEST_REMINDER,
                NotificationType.APPROVAL_NOTIFICATION);
    }

    private void checkApplicationStatus(ApplicationForm form) {
        ApplicationFormStatus status = form.getStatus();
        switch (status) {
        case VALIDATION:
        case REVIEW:
        case INTERVIEW:
        case APPROVAL:
            break;
        default:
            throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
        }
    }

    private void checkSendToPorticoStatus(ApplicationForm form, ApprovalRound approvalRound) {
        if (!form.hasEnoughReferencesToSendToPortico()
                || (!form.hasEnoughQualificationsToSendToPortico() && approvalRound.getMissingQualificationExplanation() == null)) {
            throw new IllegalStateException("Send to portico data is not valid");
        }
    }

    public void save(ApprovalRound approvalRound) {
        approvalRoundDAO.save(approvalRound);
    }

    public boolean moveToApproved(ApplicationForm form, OfferRecommendedComment offerRecommendedComment) {
        if (ApplicationFormStatus.APPROVAL != form.getStatus()) {
            throw new IllegalStateException();
        }

        if (!programInstanceService.isPrefferedStartDateWithinBounds(form)) {
            Date earliestPossibleStartDate = programInstanceService.getEarliestPossibleStartDate(form);
            if (earliestPossibleStartDate == null) {
                return false;
            }
            form.getProgrammeDetails().setStartDate(earliestPossibleStartDate);
            programmeDetailDAO.save(form.getProgrammeDetails());
        }

        form.setStatus(ApplicationFormStatus.APPROVED);
        form.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.APPROVED));
        sendNotificationToApplicant(form);
        form.removeNotificationRecord(NotificationType.APPROVAL_REMINDER);

        List<Supervisor> supervisors = form.getLatestApprovalRound().getSupervisors();
        supervisors.clear();
        supervisors.addAll(offerRecommendedComment.getSupervisors());

        applicationDAO.save(form);

        offerRecommendedComment.setApplication(form);
        offerRecommendedComment.setComment("");
        offerRecommendedComment.setType(CommentType.OFFER_RECOMMENDED_COMMENT);
        offerRecommendedComment.setUser(userService.getCurrentUser());
        for (Supervisor supervisor : offerRecommendedComment.getSupervisors()) {
            if (supervisor.getIsPrimary()) {
                offerRecommendedComment.setSupervisor(supervisor);
            } else {
                offerRecommendedComment.setSecondarySupervisor(supervisor);
            }
        }
        commentDAO.save(offerRecommendedComment);
        return true;
    }

    private void sendNotificationToApplicant(ApplicationForm form) {
        try {
            mailSendingService.sendApprovedNotification(form);
            NotificationRecord notificationRecord = form.getNotificationForType(APPLICATION_MOVED_TO_APPROVED_NOTIFICATION);
            if (notificationRecord == null) {
                notificationRecord = new NotificationRecord(APPLICATION_MOVED_TO_APPROVED_NOTIFICATION);
                form.addNotificationRecord(notificationRecord);
            }
            notificationRecord.setDate(new Date());
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    public void sendToPortico(ApplicationForm form) {
        approvedSenderService.sendToPortico(form);
    }

    public void addSupervisorInPreviousApprovalRound(ApplicationForm form, RegisteredUser newUser) {
        Supervisor supervisor = newSupervisor();
        supervisor.setUser(newUser);
        supervisorDAO.save(supervisor);
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();
        if (latestApprovalRound == null) {
            ApprovalRound approvalRound = newApprovalRound();
            approvalRound.getSupervisors().add(supervisor);
            approvalRound.setApplication(form);
            save(approvalRound);
            form.setLatestApprovalRound(approvalRound);
        } else {
            latestApprovalRound.getSupervisors().add(supervisor);
            save(latestApprovalRound);
        }
    }

    public Supervisor newSupervisor() {
        return new Supervisor();
    }

    public ApprovalRound newApprovalRound() {
        return new ApprovalRound();
    }
}
