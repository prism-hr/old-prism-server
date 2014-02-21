package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.InterviewVoteCommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.AssignInterviewersComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class InterviewService {

    private final Logger log = LoggerFactory.getLogger(InterviewService.class);

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private InterviewVoteCommentDAO interviewVoteCommentDAO;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private StageDurationService stageDurationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private ApplicationContext applicationContext;

    public void moveApplicationToInterview(RegisteredUser user, final AssignInterviewersComment interviewComment, ApplicationForm applicationForm) {
        interviewComment.setApplication(applicationForm);
        assignInterviewDueDate(interviewComment, applicationForm);

        ApplicationFormStatus previousStatus = applicationForm.getStatus();
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);

        applicationsService.save(applicationForm);

        // TODO add interview status transient field to the comment and use it here
//        if (!interview.getTakenPlace()) {
//            InterviewScheduleComment scheduleComment = commentFactory.createInterviewScheduleComment(user, applicationForm, interview.getFurtherDetails(),
//                    interview.getFurtherInterviewerDetails(), interview.getLocationURL());
//            commentService.save(scheduleComment);
//        }
//
//        if (interview.isScheduled() && !interview.getTakenPlace()) {
//            sendConfirmationEmails(interview);
//        }
//
//        if (interview.isScheduling()) {
//            createParticipants(interview);
//            mailService.sendInterviewVoteNotificationToInterviewerParticipants(interview);
//        }

        Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(applicationForm, null);
        interviewComment.setUseCustomQuestions(latestStateChangeComment.getUseCustomQuestions());
        commentService.save(interviewComment);

        if (previousStatus == ApplicationFormStatus.VALIDATION) {
            mailService.sendReferenceRequest(applicationForm.getReferees(), applicationForm);
            applicationForm.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
            applicationsService.save(applicationForm);
            applicationFormUserRoleService.validationStageCompleted(applicationForm);
        }

        applicationFormUserRoleService.movedToInterviewStage(interviewComment);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    }

    // FIXME extract common subclass for AssignInterviewersComment and InterviewScheduleComment
    protected void assignInterviewDueDate(final Comment interviewComment, ApplicationForm applicationForm) {
        Date baseDate = interviewComment.getAppointmentDate();
        if (baseDate == null) {
            baseDate = new Date();
        }
        StageDuration duration = stageDurationService.getByStatus(ApplicationFormStatus.INTERVIEW);
        Date dueDate = DateUtils.addWorkingDaysInMinutes(DateUtils.truncateToDay(baseDate), duration.getDurationInMinutes());
        applicationForm.setDueDate(dueDate);
    }

    public void postVote(InterviewParticipant interviewParticipant, InterviewVoteComment interviewVoteComment) {
        interviewParticipant.setResponded(true);
        interviewParticipantDAO.save(interviewParticipant);
        interviewVoteCommentDAO.save(interviewVoteComment);
        applicationFormUserRoleService.interviewParticipantResponded(interviewParticipant);
        applicationFormUserRoleService.registerApplicationUpdate(interviewVoteComment.getApplication(), interviewParticipant.getUser(),
                ApplicationUpdateScope.INTERNAL);
        mailService.sendInterviewVoteConfirmationToAdministrators(interviewParticipant);
    }

    public void confirmInterview(RegisteredUser user, ApplicationForm applicationForm,  InterviewConfirmDTO interviewConfirmDTO) {
        InterviewService thisBean = applicationContext.getBean(InterviewService.class);
        
        Integer timeslotId = interviewConfirmDTO.getTimeslotId();
        AppointmentTimeslot timeslot = null;
        AssignInterviewersComment assignInterviewersComment = (AssignInterviewersComment) applicationsService.getLatestStateChangeComment(applicationForm, ApplicationFormAction.ASSIGN_INTERVIEWERS);
        for (AppointmentTimeslot t : assignInterviewersComment.getAvailableAppointmentTimeslots()) {
            if (t.getId().equals(timeslotId)) {
                timeslot = t;
            }
        }

        if (timeslot == null) {
            throw new RuntimeException("Incorrect timeslotId " + timeslotId + ", application: " + applicationForm.getApplicationNumber());
        }


        InterviewScheduleComment scheduleComment = createInterviewScheduleComment(user, applicationForm, interviewConfirmDTO.getInterviewInstructions(),
                interviewConfirmDTO.getInterviewInstructions());
        commentService.save(scheduleComment);

        thisBean.assignInterviewDueDate(scheduleComment, applicationForm);
        thisBean.sendConfirmationEmails(scheduleComment);
        applicationFormUserRoleService.interviewConfirmed(interview);
        applicationFormUserRoleService.registerApplicationUpdate(application, user, ApplicationUpdateScope.ALL_USERS);
    }

    private void createParticipants(final Interview interview) {
        List<InterviewParticipant> participants = Lists.newLinkedList();
        InterviewParticipant applicant = new InterviewParticipant();
        applicant.setUser(interview.getApplication().getApplicant());
        participants.add(applicant);

        for (Interviewer interviewer : interview.getInterviewers()) {
            InterviewParticipant participant = new InterviewParticipant();
            participant.setUser(interviewer.getUser());
            participants.add(participant);
        }
        interview.getParticipants().addAll(participants);
    }

    protected void sendConfirmationEmails(InterviewScheduleComment comment) {
        final ApplicationForm applicationForm = comment.getApplication();
        try {
            mailService.sendInterviewConfirmationToApplicant(applicationForm);
            List<RegisteredUser> interviewerUsers = Lists.newArrayList();
            for(CommentAssignedUser interviewer : comment.getAssignedUsers()){
                interviewerUsers.add(interviewer.getUser());
            }
            mailService.sendInterviewConfirmationToInterviewers(applicationForm, interviewerUsers);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    private InterviewScheduleComment createInterviewScheduleComment(RegisteredUser user, ApplicationForm application, String interviewInstructions,
            String locationUrl) {
        InterviewScheduleComment scheduleComment = new InterviewScheduleComment();
        scheduleComment.setContent("");
        scheduleComment.setAppointmentInstructions(interviewInstructions);
        scheduleComment.setLocationUrl(locationUrl);
        scheduleComment.setUser(user);
        scheduleComment.setApplication(application);
        return scheduleComment;
    }

}
