package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service("applicationsService")
public class ApplicationsService {

	private final ApplicationFormDAO applicationFormDAO;

	ApplicationsService() {
		this(null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO) {
		this.applicationFormDAO = applicationFormDAO;
	}

	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
		List<ApplicationForm> visibleApplications = applicationFormDAO.getVisibleApplications(user);
		Collections.sort(visibleApplications);
		return visibleApplications;
	}

	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	@Transactional
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}
	
	@Transactional
	public ApplicationForm createAndSaveNewApplicationForm(RegisteredUser user, Program program) {

		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProgram(program);
		applicationFormDAO.save(applicationForm);
		return applicationForm;
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}
	
	@Transactional
	public List<ApplicationForm> getApplicationsDueUpdateNotification() {
		return applicationFormDAO.getApplicationsDueUpdateNotification();
	}

	public ApplicationFormStatus getStageComingFrom(ApplicationForm application) {
		List<Event> events = application.getEventsSortedByDate();
		Event previousEvent;
		if(events.size() == 1 ){
			return events.get(0).getNewStatus();
		}
		if(events.size() == 2){
			if(events.get(1).getNewStatus() == ApplicationFormStatus.REJECTED){
				return events.get(0).getNewStatus();
			}
			else{
				return events.get(0).getNewStatus();
			}
		}
		if(events.size()>2){
			for(int i=0; i<events.size(); i++){
				if(events.get(i).getNewStatus() == ApplicationFormStatus.REJECTED ){
					previousEvent = events.get(i - 1);
					if(previousEvent.getNewStatus() ==  ApplicationFormStatus.REVIEW || previousEvent.getNewStatus() ==  ApplicationFormStatus.INTERVIEW){
						return previousEvent.getNewStatus();
					}
					if(previousEvent.getNewStatus() ==  ApplicationFormStatus.APPROVAL){
						Event previousOfApproval = events.get(i - 2);
						if(previousOfApproval.getNewStatus() ==  ApplicationFormStatus.REVIEW || previousOfApproval.getNewStatus() ==  ApplicationFormStatus.INTERVIEW || previousOfApproval.getNewStatus() ==  ApplicationFormStatus.VALIDATION){
							return previousOfApproval.getNewStatus();
						}
					}
				}
			}
		}
		return null;
	}



}
