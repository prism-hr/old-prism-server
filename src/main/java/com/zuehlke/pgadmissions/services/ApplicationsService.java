package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Service("applicationsService")
@Transactional
public class ApplicationsService {

	public static final int APPLICATION_BLOCK_SIZE = 50;

	private final ApplicationFormDAO applicationFormDAO;

	private final ApplicationFormListDAO applicationFormListDAO;

	public ApplicationsService() {
		this(null, null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO, ApplicationFormListDAO applicationFormListDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.applicationFormListDAO = applicationFormListDAO;
	}

	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
		return applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
	}

	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);
	}

	public ApplicationForm createOrGetUnsubmittedApplicationForm(RegisteredUser user, Program program, Date programDeadline,
			String projectTitle, String researchHomePage) {

		ApplicationForm applicationForm = findUnsubmittedApplication(user, program);
		if (applicationForm != null) {
			return applicationForm;
		}

		String thisYear = new SimpleDateFormat("yyyy").format(new Date());

		applicationForm = new ApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProgram(program);
		applicationForm.setBatchDeadline(programDeadline);

		applicationForm.setProjectTitle(projectTitle);
		applicationForm.setResearchHomePage(researchHomePage);
		Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
		applicationForm.setApplicationNumber(program.getCode() + "-" + thisYear + "-"
				+ String.format("%06d", ++runningCount));
		applicationFormDAO.save(applicationForm);
		return applicationForm;
	}

	private ApplicationForm findUnsubmittedApplication(RegisteredUser user, Program program) {
		List<ApplicationForm> applications = applicationFormDAO.getApplicationsByApplicantAndProgram(user, program);
		if (!applications.isEmpty()) {
			Collections.sort(applications, new Comparator<ApplicationForm>() {
				@Override
				public int compare(ApplicationForm o1, ApplicationForm o2) {
					return o1.getApplicationTimestamp().compareTo(o2.getApplicationTimestamp());
				}
			});
			for(ApplicationForm applicationForm : applications){
				if(!applicationForm.isDecided() && !applicationForm.isWithdrawn()){
					return applicationForm;
				}
			}
		}
		return null;
	}

	public void makeApplicationNotEditable(ApplicationForm applicationForm) {
		applicationForm.setIsEditableByApplicant(false);
	}

	public List<ApplicationForm> getApplicationsDueUpdateNotification() {
		return applicationFormDAO.getApplicationsDueUpdateNotification();
	}

	public List<ApplicationForm> getAllVisibleAndMatchedApplications(RegisteredUser user,
			SearchCategory searchCategory, String term, SortCategory sort, SortOrder order, Integer page) {
		// default values
		int pageCount = page == null ? 1 : page;
		SortCategory sortCategory = sort == null ? SortCategory.APPLICATION_DATE : sort;
		SortOrder sortOrder = order == null ? SortOrder.ASCENDING : order;
		if (pageCount < 0) {
			pageCount = 0;
		}
		return applicationFormListDAO.getVisibleApplications(user, searchCategory, term, sortCategory, sortOrder,
				pageCount, APPLICATION_BLOCK_SIZE);
	}

	public void refresh(ApplicationForm applicationForm) {
		applicationFormDAO.refresh(applicationForm);
	}

	public List<ApplicationForm> getApplicationsDueRegistryNotification() {
		return applicationFormDAO.getApplicationsDueRegistryNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestNotification() {
		return applicationFormDAO.getApplicationsDueApprovalRequestNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestReminder() {
		return applicationFormDAO.getApplicationDueApprovalRestartRequestReminder();
	}

	public List<ApplicationForm> getAllApplicationsByStatus(ApplicationFormStatus status) {
		return applicationFormDAO.getAllApplicationsByStatus(status);
	}

}
