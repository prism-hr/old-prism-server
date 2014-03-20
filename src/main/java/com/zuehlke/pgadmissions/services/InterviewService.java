package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.dao.InterviewParticipantDAO;
import com.zuehlke.pgadmissions.dao.InterviewVoteCommentDAO;
import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewScheduleComment;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class InterviewService {

    private final Logger log = LoggerFactory.getLogger(InterviewService.class);
    private final InterviewDAO interviewDAO;
    private final ApplicationFormDAO applicationFormDAO;
    private final EventFactory eventFactory;
    private final InterviewerDAO interviewerDAO;
    private final InterviewParticipantDAO interviewParticipantDAO;
    private final InterviewVoteCommentDAO interviewVoteCommentDAO;
    private final MailSendingService mailService;
    private final StageDurationService stageDurationService;
    private final CommentService commentService;
    private final CommentFactory commentFactory;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public InterviewService() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public InterviewService(InterviewDAO interviewDAO, ApplicationFormDAO applicationFormDAO, EventFactory eventFactory, InterviewerDAO interviewerDAO,
            InterviewParticipantDAO interviewParticipantDAO, MailSendingService mailService, InterviewVoteCommentDAO interviewVoteCommentDAO,
            final StageDurationService stageDurationService, CommentService commentService, CommentFactory commentFactory, 
            ApplicationFormUserRoleService ApplicationFormUserRoleService) {
        this.interviewDAO = interviewDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.eventFactory = eventFactory;
        this.interviewerDAO = interviewerDAO;
        this.interviewParticipantDAO = interviewParticipantDAO;
        this.mailService = mailService;
        this.interviewVoteCommentDAO = interviewVoteCommentDAO;
        this.stageDurationService = stageDurationService;
        this.commentService = commentService;
        this.commentFactory = commentFactory;
        this.applicationFormUserRoleService = ApplicationFormUserRoleService;
    }

    public Interview getInterviewById(Integer id) {
        return interviewDAO.getInterviewById(id);
    }

    public void save(Interview interview) {
        interviewDAO.save(interview);
    }

    public void moveApplicationToInterview(RegisteredUser user, final Interview interview, ApplicationForm applicationForm) {
        interview.setApplication(applicationForm);
        assignInterviewDueDate(interview, applicationForm);

        for (Interviewer interviewer : interview.getInterviewers()) {
            interviewer.setInterview(interview);
            interviewerDAO.save(interviewer);
        }
        
        applicationForm.setLatestInterview(interview);
        ApplicationFormStatus previousStatus = applicationForm.getStatus();
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
        applicationForm.getEvents().add(eventFactory.createEvent(interview));

        applicationFormDAO.save(applicationForm);

        if (!interview.getTakenPlace()) {
            InterviewScheduleComment scheduleComment = commentFactory.createInterviewScheduleComment(user, applicationForm, interview.getFurtherDetails(),
                    interview.getFurtherInterviewerDetails(), interview.getLocationURL());
            commentService.save(scheduleComment);
        }

        if (interview.isScheduled() && !interview.getTakenPlace()) {
            sendConfirmationEmails(interview);
        }

        if (interview.isScheduling()) {
            createParticipants(interview);
            mailService.sendInterviewVoteNotificationToInterviewerParticipants(interview);
        }

        StateChangeComment latestStateChangeComment = applicationForm.getLatestStateChangeComment();
        interview.setUseCustomQuestions(latestStateChangeComment.getUseCustomQuestions());
        interviewDAO.save(interview);
        
        if (previousStatus == ApplicationFormStatus.VALIDATION) {
            mailService.sendReferenceRequest(applicationForm.getReferees(), applicationForm);
            applicationForm.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
            applicationFormDAO.save(applicationForm);
            applicationFormUserRoleService.validationStageCompleted(applicationForm);
        }
        
        applicationFormUserRoleService.movedToInterviewStage(interview);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    }

    private void assignInterviewDueDate(final Interview interview, ApplicationForm applicationForm) {
        DateTime baseDate = interview.getTakenPlace() || !interview.isScheduled() ? new DateTime() : new DateTime(interview.getInterviewDueDate());
        StageDuration duration = stageDurationService.getById(ApplicationFormStatus.INTERVIEW);
        Date dueDate = DateUtils.addWorkingDaysInMinutes(DateUtils.truncateToDay(baseDate.toDate()), duration.getDurationInMinutes());
        applicationForm.setDueDate(dueDate);
    }

    public void postVote(InterviewParticipant interviewParticipant, InterviewVoteComment interviewVoteComment) {
        interviewParticipant.setResponded(true);
        interviewParticipantDAO.save(interviewParticipant);
        interviewVoteCommentDAO.save(interviewVoteComment);
        applicationFormUserRoleService.interviewParticipantResponded(interviewParticipant);
        applicationFormUserRoleService.insertApplicationUpdate(interviewVoteComment.getApplication(), interviewParticipant.getUser(), ApplicationUpdateScope.INTERNAL);
        mailService.sendInterviewVoteConfirmationToAdministrators(interviewParticipant);
    }

    public void confirmInterview(RegisteredUser user, Interview interview, InterviewConfirmDTO interviewConfirmDTO) {
        Integer timeslotId = interviewConfirmDTO.getTimeslotId();
        InterviewTimeslot timeslot = null;
        for (InterviewTimeslot t : interview.getTimeslots()) {
            if (t.getId().equals(timeslotId)) {
                timeslot = t;
            }
        }
        if (timeslot == null) {
            throw new RuntimeException("Incorrect timeslotId " + timeslotId + ", application: " + interview.getApplication().getApplicationNumber());
        }

        interview.setInterviewDueDate(timeslot.getDueDate());
        interview.setInterviewTime(timeslot.getStartTime());
        interview.setFurtherDetails(interviewConfirmDTO.getFurtherDetails());
        interview.setFurtherInterviewerDetails(interviewConfirmDTO.getFurtherInterviewerDetails());
        interview.setLocationURL(interviewConfirmDTO.getLocationUrl());
        interview.setStage(InterviewStage.SCHEDULED);
        interviewDAO.save(interview);

        InterviewScheduleComment scheduleComment = commentFactory.createInterviewScheduleComment(user, interview.getApplication(),
                interview.getFurtherDetails(), interview.getFurtherInterviewerDetails(), interview.getLocationURL());
        commentService.save(scheduleComment);
        
        ApplicationForm application = interview.getApplication(); 
        assignInterviewDueDate(interview, application);
        sendConfirmationEmails(interview);    
        applicationFormUserRoleService.interviewConfirmed(interview);
        applicationFormUserRoleService.insertApplicationUpdate(application, user, ApplicationUpdateScope.ALL_USERS);
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

    private void sendConfirmationEmails(final Interview interview) {
        final ApplicationForm applicationForm = interview.getApplication();
        try {
            mailService.sendInterviewConfirmationToApplicant(applicationForm);
            mailService.sendInterviewConfirmationToInterviewers(interview.getInterviewers());
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    public void addInterviewerInPreviousInterview(ApplicationForm applicationForm, RegisteredUser newUser) {
        Interviewer inter = newInterviewer();
        inter.setUser(newUser);
        interviewerDAO.save(inter);
        Interview latestInterview = applicationForm.getLatestInterview();
        if (latestInterview == null) {
            Interview interview = newInterview();
            interview.getInterviewers().add(inter);
            interview.setApplication(applicationForm);
            save(interview);
            applicationForm.setLatestInterview(interview);
        } else {
            latestInterview.getInterviewers().add(inter);
            save(latestInterview);
        }
    }

    public Interviewer newInterviewer() {
        return new Interviewer();
    }

    public Interview newInterview() {
        return new Interview();
    }

}