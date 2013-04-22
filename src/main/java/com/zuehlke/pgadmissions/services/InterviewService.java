package com.zuehlke.pgadmissions.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class InterviewService {

    private final InterviewDAO interviewDAO;
    private final ApplicationFormDAO applicationFormDAO;
    private final EventFactory eventFactory;
    private final InterviewerDAO interviewerDAO;
    private final MailSendingService mailService;

    public InterviewService() {
        this(null, null, null, null, null);
    }

    @Autowired
    public InterviewService(InterviewDAO interviewDAO, ApplicationFormDAO applicationFormDAO,
            EventFactory eventFactory, InterviewerDAO interviewerDAO, final MailSendingService mailService) {
        this.interviewDAO = interviewDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.eventFactory = eventFactory;
        this.interviewerDAO = interviewerDAO;
		this.mailService = mailService;
    }

    public Interview getInterviewById(Integer id) {
        return interviewDAO.getInterviewById(id);
    }

    public void save(Interview interview) {
        interviewDAO.save(interview);
    }

    public void moveApplicationToInterview(Interview interview, ApplicationForm applicationForm) {
        interview.setApplication(applicationForm);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(interview.getInterviewDueDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        applicationForm.setDueDate(calendar.getTime());
        mailService.sendInterviewConfirmationToApplicant(applicationForm);
        mailService.sendInterviewConfirmationToInterviewers(interview.getInterviewers());
        interviewDAO.save(interview);
        applicationForm.setLatestInterview(interview);
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
        applicationForm.getEvents().add(eventFactory.createEvent(interview));
        NotificationRecord interviewReminderRecord = applicationForm
                .getNotificationForType(NotificationType.INTERVIEW_REMINDER);
        //Check if the interview administration was delegated
        if (applicationForm.getApplicationAdministrator()!=null) {
        	//We remove the notification record so that the delegate does not receive reminders any longer
        	NotificationRecord interviewAdministrationReminderRecord = applicationForm
        			.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER);
        	applicationForm.removeNotificationRecord(interviewAdministrationReminderRecord);
        	applicationForm.setApplicationAdministrator(null);
        	applicationForm.setSuppressStateChangeNotifications(false);
        }
        if (interviewReminderRecord != null) {
            applicationForm.removeNotificationRecord(interviewReminderRecord);
        }
        applicationFormDAO.save(applicationForm);
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
