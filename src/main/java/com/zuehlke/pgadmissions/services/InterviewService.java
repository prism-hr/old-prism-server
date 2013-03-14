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

@Service
@Transactional
public class InterviewService {

    private final InterviewDAO interviewDAO;
    private final ApplicationFormDAO applicationFormDAO;
    private final EventFactory eventFactory;
    private final InterviewerDAO interviewerDAO;

    public InterviewService() {
        this(null, null, null, null);
    }

    @Autowired
    public InterviewService(InterviewDAO interviewDAO, ApplicationFormDAO applicationFormDAO,
            EventFactory eventFactory, InterviewerDAO interviewerDAO) {
        this.interviewDAO = interviewDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.eventFactory = eventFactory;
        this.interviewerDAO = interviewerDAO;
    }

    public Interview getInterviewById(Integer id) {
        return interviewDAO.getInterviewById(id);
    }

    public void save(Interview interview) {
        interviewDAO.save(interview);
    }

    public void moveApplicationToInterview(Interview interview, ApplicationForm applicationForm) {
        checkApplicationStatus(applicationForm);
        interview.setApplication(applicationForm);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(interview.getInterviewDueDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        applicationForm.setDueDate(calendar.getTime());
        interviewDAO.save(interview);
        applicationForm.setLatestInterview(interview);
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
        applicationForm.getEvents().add(eventFactory.createEvent(interview));
        NotificationRecord interviewReminderRecord = applicationForm
                .getNotificationForType(NotificationType.INTERVIEW_REMINDER);
        if (interviewReminderRecord != null) {
            applicationForm.removeNotificationRecord(interviewReminderRecord);
        }
        applicationFormDAO.save(applicationForm);
    }

    private void checkApplicationStatus(ApplicationForm application) {
        ApplicationFormStatus status = application.getStatus();
        switch (status) {
        case VALIDATION:
        case REVIEW:
        case INTERVIEW:
            break;
        default:
            throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
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
