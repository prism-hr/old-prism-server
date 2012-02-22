package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@RemoteProxy(name="dwrApplicationsService")
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
	@RemoteMethod
	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
		List<ApplicationForm> visibleApplications = new ArrayList<ApplicationForm>();

		if (user.isInRole(Authority.APPLICANT)) {
			List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
			applications = applicationFormDAO.getApplicationsByUser(user);
			if (applications != null) {
				visibleApplications.addAll(applications);
			}
		} else {
			List<ApplicationForm> applications = applicationFormDAO
					.getAllApplications();
			if (applications != null) {
				for (ApplicationForm application : applications) {
					if (user.canSee(application)) {
						visibleApplications.add(application);
					}
				}
			}
		}

		return visibleApplications;
	}

	@Transactional
	@RemoteMethod
	public ApplicationForm getApplicationById(int id) {
		return applicationFormDAO.get(id);
	}

	@Transactional
	@RemoteMethod
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}

}
