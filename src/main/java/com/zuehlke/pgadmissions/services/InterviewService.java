package com.zuehlke.pgadmissions.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.EventFactory;

@Service
public class InterviewService {

	private final InterviewDAO interviewDAO;
	private final ApplicationFormDAO applicationFormDAO;
	private final EventFactory eventFactory;
	
	InterviewService() {
		this(null, null, null);
	}
	
	@Autowired
	public InterviewService(InterviewDAO interviewDAO, ApplicationFormDAO applicationFormDAO, EventFactory eventFactory){
		this.interviewDAO = interviewDAO;
		this.applicationFormDAO = applicationFormDAO;
		this.eventFactory = eventFactory;
	}
	
	@Transactional
	public Interview getInterviewById(Integer id) {
		return interviewDAO.getInterviewById(id);
	}

	@Transactional
	public void save(Interview interview) {
		interviewDAO.save(interview);
	}

	@Transactional
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

}
