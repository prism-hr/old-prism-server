package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApprovalService {

    private final ApplicationFormDAO applicationDAO;

    private final ApprovalRoundDAO approvalRoundDAO;

    private final StageDurationService stageDurationService;

    private final EventFactory eventFactory;

    private final CommentDAO commentDAO;

    private final UserService userService;

    private final MailSendingService mailSendingService;

    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public ApprovalService() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ApprovalService(UserService userService, ApplicationFormDAO applicationDAO, ApprovalRoundDAO approvalRoundDAO,
            StageDurationService stageDurationService, EventFactory eventFactory, CommentDAO commentDAO, MailSendingService mailSendingService,
            ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.userService = userService;
        this.applicationDAO = applicationDAO;
        this.approvalRoundDAO = approvalRoundDAO;
        this.stageDurationService = stageDurationService;
        this.eventFactory = eventFactory;
        this.commentDAO = commentDAO;
        this.mailSendingService = mailSendingService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    public void confirmOrDeclineSupervision(ApplicationForm form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalRound approvalRound = form.getLatestApprovalRound();
        Supervisor supervisor = approvalRound.getPrimarySupervisor();
        Boolean confirmed = confirmSupervisionDTO.getConfirmedSupervision();

        supervisor.setConfirmedSupervision(confirmed);

        if (BooleanUtils.isTrue(confirmed)) {
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

        applicationFormUserRoleService.supervisionConfirmed(supervisor);
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

    public void moveApplicationToApproval(ApplicationForm form, ApprovalRound approvalRound, RegisteredUser initiator) {
        checkApplicationStatus(form);
        checkSendToPorticoStatus(form, approvalRound);
        form.setLatestApprovalRound(approvalRound);
        approvalRound.setApplication(form);
        approvalRoundDAO.save(approvalRound);

        StageDuration approveStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.APPROVAL);
        DateTime dueDate = DateUtils.addWorkingDaysInMinutes(new DateTime(), approveStageDuration.getDurationInMinutes());
        form.setDueDate(dueDate.toDate());

        form.getEvents().add(eventFactory.createEvent(approvalRound));

        boolean sendReferenceRequest = form.getStatus() == ApplicationFormStatus.VALIDATION;

        form.setStatus(ApplicationFormStatus.APPROVAL);
        applicationDAO.save(form);
        
        RegisteredUser mover = userService.getCurrentUser();

        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setApplication(form);
        approvalComment.setComment(StringUtils.EMPTY);
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setProjectAbstract(approvalRound.getProjectAbstract());
        approvalComment.setProjectTitle(approvalRound.getProjectTitle());
        approvalComment.setRecommendedConditions(approvalRound.getRecommendedConditions());
        approvalComment.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        approvalComment.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
        approvalComment.setSecondarySupervisor(approvalRound.getSecondarySupervisor());
        approvalComment.setUser(mover);

        if (sendReferenceRequest) {
            mailSendingService.sendReferenceRequest(form.getReferees(), form);
            applicationFormUserRoleService.validationStageCompleted(form);
        }
        commentDAO.save(approvalComment);
        applicationFormUserRoleService.movedToApprovalStage(approvalRound, mover);
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
        if (!form.hasEnoughReferencesToSendToPortico() || (!form.hasEnoughQualificationsToSendToPortico() && approvalRound.getMissingQualificationExplanation() == null)) {
            throw new IllegalStateException("Export data is not valid");
        }
    }

}