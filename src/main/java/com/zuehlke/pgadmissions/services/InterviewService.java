package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.domain.Interview;

@Service
public class InterviewService {

	private final InterviewDAO interviewDAO;
	
	InterviewService() {
		this(null);
	}
	
	@Autowired
	public InterviewService(InterviewDAO interviewDAO){
		this.interviewDAO = interviewDAO;
	}
	
	@Transactional
	public Interview getInterviewById(Integer id) {
		return interviewDAO.getInterviewById(id);
	}

	@Transactional
	public void save(Interview interview) {
		interviewDAO.save(interview);
	}
}
