package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

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

	@Transactional
	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
		List<ApplicationForm> visibleApplications = new ArrayList<ApplicationForm>();

		if (user.isInRole(Authority.APPLICANT)) {
			List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
			applications = applicationFormDAO.getApplicationsByApplicant(user);
			if (applications != null) {
				visibleApplications.addAll(applications);
			}
		} else {
			List<ApplicationForm> applications = applicationFormDAO.getAllApplications();
			if (applications != null) {
				for (ApplicationForm application : applications) {
					if (user.canSee(application)) {
						visibleApplications.add(application);
					}
				}
			}
		}
		
		Collections.sort(visibleApplications);
		return visibleApplications;
	}

	@Transactional
	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	@Transactional
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}
	
	@Transactional
	public Qualification getQualificationById(Integer id) {
		return applicationFormDAO.getQualification(id);
	}

	@Transactional
	public List<Qualification> getQualificationsByApplication(
			ApplicationForm applicationForm) {
		return applicationFormDAO.getQualificationsByApplication(applicationForm);
	}

	@Transactional
	public com.zuehlke.pgadmissions.domain.Funding getFundingById(Integer fundingId) {
		return applicationFormDAO.getFundingById(fundingId);
	}
	
	@Transactional
	public EmploymentPosition getEmploymentPositionById(Integer positionId) {
		return applicationFormDAO.getEmploymentById(positionId);
	}

}
