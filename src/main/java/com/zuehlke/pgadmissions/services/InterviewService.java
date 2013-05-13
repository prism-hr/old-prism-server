package com.zuehlke.pgadmissions.services;

import java.util.Calendar;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MailSendingService;

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

    public InterviewService() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public InterviewService(InterviewDAO interviewDAO, ApplicationFormDAO applicationFormDAO, EventFactory eventFactory, InterviewerDAO interviewerDAO,
            InterviewParticipantDAO interviewParticipantDAO, MailSendingService mailService, InterviewVoteCommentDAO interviewVoteCommentDAO) {
        this.interviewDAO = interviewDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.eventFactory = eventFactory;
        this.interviewerDAO = interviewerDAO;
        this.interviewParticipantDAO = interviewParticipantDAO;
        this.mailService = mailService;
        this.interviewVoteCommentDAO = interviewVoteCommentDAO;
    }

    public Interview getInterviewById(Integer id) {
        return interviewDAO.getInterviewById(id);
    }

    public void save(Interview interview) {
        interviewDAO.save(interview);
    }

    public void moveApplicationToInterview(final Interview interview, ApplicationForm applicationForm) {
        interview.setApplication(applicationForm);
        if (interview.getInterviewDueDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(interview.getInterviewDueDate());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            applicationForm.setDueDate(calendar.getTime());
        }
        interviewDAO.save(interview);

        for (Interviewer interviewer : interview.getInterviewers()) {
            interviewer.setInterview(interview);
            interviewerDAO.save(interviewer);
        }

        applicationForm.setLatestInterview(interview);
        ApplicationFormStatus previousStatus = applicationForm.getStatus();
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
        applicationForm.getEvents().add(eventFactory.createEvent(interview));

        // Check if the interview administration was delegated
        if (applicationForm.getApplicationAdministrator() != null) {
            // We remove the notification record so that the delegate does not receive reminders any longer
            applicationForm.removeNotificationRecord(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST, NotificationType.INTERVIEW_ADMINISTRATION_REMINDER);
            applicationForm.setApplicationAdministrator(null);
            applicationForm.setSuppressStateChangeNotifications(false);
        }
        applicationForm.removeNotificationRecord(NotificationType.INTERVIEW_FEEDBACK_REMINDER);

        applicationFormDAO.save(applicationForm);

        if (interview.isScheduled()) {
            sendConfirmationEmails(interview);
        } else if (interview.isScheduling()) {
            createParticipants(interview);
            mailService.sendInterviewVoteNotificationToInterviewerParticipants(interview);
        }

        if (previousStatus == ApplicationFormStatus.VALIDATION) {
            mailService.sendReferenceRequest(applicationForm.getReferees(), applicationForm);
        }

    }

    public void postVote(InterviewParticipant interviewParticipant, InterviewVoteComment interviewVoteComment) {
        interviewParticipant.setResponded(true);
        interviewParticipantDAO.save(interviewParticipant);
        interviewVoteCommentDAO.save(interviewVoteComment);
    }

    public void confirmInterview(Interview interview, Integer timeslotId) {
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
        interview.setStage(InterviewStage.SCHEDULED);
        interviewDAO.save(interview);

        sendConfirmationEmails(interview);
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
