package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
public class InterviewerService {
	
	private final InterviewerDAO interviewerDAO;
	
	
	InterviewerService() {
		this(null);
	}
	
	@Autowired
	public InterviewerService(InterviewerDAO interviewerDAO){
		this.interviewerDAO = interviewerDAO;
	}
	

	@Transactional
	public Interviewer getInterviewerById(Integer id) {
		return interviewerDAO.getInterviewerById(id);
	}

	@Transactional
	public void save(Interviewer interviewer) {
		interviewerDAO.save(interviewer);
	}

	@Transactional
	public Interviewer getInterviewerByUser(RegisteredUser user) {
		return interviewerDAO.getInterviewerByUser(user);
	}

}
