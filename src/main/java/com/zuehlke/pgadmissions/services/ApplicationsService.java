package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AddressDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service("applicationsService")
public class ApplicationsService {

	private final ApplicationFormDAO applicationFormDAO;
	private final AddressDAO addressDAO;

	ApplicationsService() {
		this(null, null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO, AddressDAO addressDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.addressDAO = addressDAO;
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

	public com.zuehlke.pgadmissions.domain.Address getAddressById(Integer addressId) {
		return applicationFormDAO.getAdddressById(addressId);
	}


	@Transactional
	public void deleteAddress(Address address) {
		addressDAO.delete(address);
	}
	
	@Transactional
	public ApplicationForm createAndSaveNewApplicationForm(RegisteredUser user, Project project) {

		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProject(project);
		applicationFormDAO.save(applicationForm);
		return applicationForm;
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

}
