package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class InterviewerService {
	
	private final InterviewerDAO interviewerDAO;
	private final UserService userService;
	private final ProgramsService programService;
	
	InterviewerService() {
		this(null, null, null);
	}
	
	@Autowired
	public InterviewerService(InterviewerDAO interviewerDAO, UserService userService, ProgramsService programService){
		this.interviewerDAO = interviewerDAO;
		this.userService = userService;
		this.programService = programService;
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

	@Transactional
	public RegisteredUser createNewUserWithInterviewerRoleInProgram(RegisteredUser interviewer, Program program) {
		return userService.createNewUserForProgramme(interviewer.getFirstName(), interviewer.getLastName(), interviewer.getEmail(), program, Authority.INTERVIEWER);
	}


	@Transactional
	public void addInterviewerToProgram(RegisteredUser interviewerUser, Program program) {
		if (!interviewerUser.isInRole(Authority.INTERVIEWER)) {
			userService.addRoleToUser(interviewerUser, Authority.INTERVIEWER);
		}
		addAndSaveInterviewerToProgram(program, interviewerUser);
	}

	private void addAndSaveInterviewerToProgram(Program program,
			RegisteredUser newUser) {
		newUser.getProgramsOfWhichInterviewer().add(program);
		program.getInterviewers().add(newUser);
		userService.save(newUser);
		programService.save(program);
	}

	@Transactional
	public void createInterviewerToApplication(RegisteredUser interviewerUser, ApplicationForm application) {
		Interviewer interviewer = createNewInterviewer();
		interviewer.setUser(interviewerUser);
		interviewer.setApplication(application);
		application.getCurrentInterview().getInterviewers().add(interviewer);
		System.err.println("saving: " +  interviewer);
		interviewerDAO.save(interviewer);
		
	}

	public Interviewer createNewInterviewer() {
		return new Interviewer();
	}

}
