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
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
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


	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	@Transactional
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}


	public Qualification getQualificationById(Integer id) {
		return applicationFormDAO.getQualification(id);
	}


	public Referee getRefereeById(Integer id) {
		return applicationFormDAO.getRefereeById(id);
	}


	public List<Qualification> getQualificationsByApplication(ApplicationForm applicationForm) {
		return applicationFormDAO.getQualificationsByApplication(applicationForm);
	}


	public com.zuehlke.pgadmissions.domain.Funding getFundingById(Integer fundingId) {
		return applicationFormDAO.getFundingById(fundingId);
	}


	public EmploymentPosition getEmploymentPositionById(Integer positionId) {
		return applicationFormDAO.getEmploymentById(positionId);
	}


	public com.zuehlke.pgadmissions.domain.Address getAddressById(Integer addressId) {
		return applicationFormDAO.getAdddressById(addressId);
	}

	@Transactional
	public void update(Qualification qualification) {
		applicationFormDAO.update(qualification);

	}


	public Messenger getMessengerById(Integer id) {
		return applicationFormDAO.getMessengerById(id);
	}


	public Telephone getTelephoneById(Integer id) {
		return applicationFormDAO.getTelephoneById(id);
	}

	@Transactional
	public void saveTelephone(Telephone telephone) {
		applicationFormDAO.saveTelephone(telephone);

	}

	@Transactional
	public void saveMessenger(Messenger messenger) {
		applicationFormDAO.saveMessenger(messenger);

	}

	@Transactional
	public void saveReferee(com.zuehlke.pgadmissions.domain.Referee referee) {
		applicationFormDAO.saveReferee(referee);

	}


}
