package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class InterviewService {

    private final Logger log = LoggerFactory.getLogger(InterviewService.class);

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationContext applicationContext;

    public void moveApplicationToInterview(User user, final Comment interviewComment, Application applicationForm) {
        interviewComment.setApplication(applicationForm);
        // TODO: remove class and integrate with workflow engine
        // applicationsService.setApplicationStatus(applicationForm, PrismState.APPLICATION_INTERVIEW);

        // TODO add interview status transient field to the comment and use it here
        // if (!interview.getTakenPlace()) {
        // InterviewScheduleComment scheduleComment = commentFactory.createInterviewScheduleComment(user, applicationForm, interview.getFurtherDetails(),
        // interview.getFurtherInterviewerDetails(), interview.getLocationURL());
        // commentService.save(scheduleComment);
        // }
        //
        // if (interview.isScheduled() && !interview.getTakenPlace()) {
        // sendConfirmationEmails(interview);
        // }
        //
        // if (interview.isScheduling()) {
        // createParticipants(interview);
        // mailService.sendInterviewVoteNotificationToInterviewerParticipants(interview);
        // }

        Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(applicationForm, null);
        interviewComment.setUseCustomRecruiterQuestions(latestStateChangeComment.getUseCustomRecruiterQuestions());
        commentService.save(interviewComment);

        // TODO move into common place
        // if (previousStatus == ApplicationFormStatus.VALIDATION) {
        // mailService.sendReferenceRequest(applicationForm.getReferees(), applicationForm);
        // applicationForm.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
        // applicationsService.save(applicationForm);
        // applicationFormUserRoleService.validationStageCompleted(applicationForm);
        // }

    }

    public void postVote(Comment interviewVoteComment, User user) {
        Application application = interviewVoteComment.getApplication();
        Comment assignInterviewersComment = applicationsService.getLatestStateChangeComment(application, PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        commentService.save(interviewVoteComment);
        mailService.sendInterviewVoteConfirmationToAdministrators(application, user);
    }

    public void confirmInterview(User user, Application applicationForm, InterviewConfirmDTO interviewConfirmDTO) {
        InterviewService thisBean = applicationContext.getBean(InterviewService.class);

        Integer timeslotId = interviewConfirmDTO.getTimeslotId();
        AppointmentTimeslot timeslot = null;
        Comment assignInterviewersComment = applicationsService.getLatestStateChangeComment(applicationForm, PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        for (AppointmentTimeslot t : assignInterviewersComment.getAppointmentTimeslots()) {
            if (t.getId().equals(timeslotId)) {
                timeslot = t;
            }
        }

        if (timeslot == null) {
            throw new RuntimeException("Incorrect timeslotId " + timeslotId + ", application: " + applicationForm.getCode());
        }

        Comment scheduleComment = createInterviewScheduleComment(user, applicationForm, interviewConfirmDTO.getInterviewInstructions(),
                interviewConfirmDTO.getInterviewInstructions());
        commentService.save(scheduleComment);

        // TODO set due date
        // thisBean.assignInterviewDueDate(scheduleComment, applicationForm);
        thisBean.sendConfirmationEmails(scheduleComment);
    }

    // FIXME change to createAssignedUsers, used in moveApplicationToInterview() method
    // private void createParticipants(final Interview interview) {
    // List<InterviewParticipant> participants = Lists.newLinkedList();
    // InterviewParticipant applicant = new InterviewParticipant();
    // applicant.setUser(interview.getApplication().getApplicant());
    // participants.add(applicant);
    //
    // for (Interviewer interviewer : interview.getInterviewers()) {
    // InterviewParticipant participant = new InterviewParticipant();
    // participant.setUser(interviewer.getUser());
    // participants.add(participant);
    // }
    // interview.getParticipants().addAll(participants);
    // }

    protected void sendConfirmationEmails(Comment comment) {
        final Application applicationForm = comment.getApplication();
        try {
            mailService.sendInterviewConfirmationToApplicant(applicationForm);
            List<User> interviewerUsers = Lists.newArrayList();
            for (CommentAssignedUser interviewer : comment.getCommentAssignedUsers()) {
                interviewerUsers.add(interviewer.getUser());
            }
            mailService.sendInterviewConfirmationToInterviewers(applicationForm, interviewerUsers);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    private Comment createInterviewScheduleComment(User user, Application application, String interviewInstructions, String locationUrl) {
        Comment scheduleComment = new Comment();
        scheduleComment.setContent("");
        scheduleComment.setIntervieweeInstructions(interviewInstructions);
        scheduleComment.setInterviewLocation(locationUrl);
        scheduleComment.setUser(user);
        scheduleComment.setApplication(application);
        return scheduleComment;
    }

}
