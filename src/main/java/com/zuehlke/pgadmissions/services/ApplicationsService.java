package com.zuehlke.pgadmissions.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Service("applicationsService")
public class ApplicationsService {
	private final int APPLICATION_BLOCK_SIZE = 25;

	private final ApplicationFormDAO applicationFormDAO;

	ApplicationsService() {
		this(null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO) {
		this.applicationFormDAO = applicationFormDAO;
	}

	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
		return applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
	}

	@Transactional
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}

	@Transactional
	public ApplicationForm createAndSaveNewApplicationForm(RegisteredUser user, Program program, Date programDeadline, String projectTitle, String researchHomePage)  {
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProgram(program);	
		applicationForm.setBatchDeadline(programDeadline);
		
		applicationForm.setProjectTitle(projectTitle);
		applicationForm.setResearchHomePage(researchHomePage);
		int runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
		applicationForm.setApplicationNumber(program.getCode() + "-" + thisYear + "-" + String.format("%06d", ++runningCount));
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
		List<StateChangeEvent> events = application.getStateChangeEventsSortedByDate();
		if (events.isEmpty() ) {
			return null;
		}
		if (ApplicationFormStatus.REJECTED == application.getStatus()) {
			StateChangeEvent stateChangeEvent = events.get(events.size() - 2);
			if (stateChangeEvent.getNewStatus() != ApplicationFormStatus.APPROVAL) {
				return stateChangeEvent.getNewStatus();
			}
			return events.get(events.size() - 3).getNewStatus();
		}
		if (ApplicationFormStatus.APPROVAL == application.getStatus()) {
			return events.get(events.size() - 2).getNewStatus();		
		}		
		return application.getStatus();
	}

	/**
	 * Returns all applications matching the given parameters
	 * 
	 * @param user
	 * @param searchCategory
	 * @param term
	 * @param sortCategory
	 * @param sortOrder
	 * @param blockCount
	 *            number of blocks of applications which should be returned (see
	 *            {@link #APPLICATION_BLOCK_SIZE}).
	 * @return
	 */
	@Transactional
	public List<ApplicationForm> getAllVisibleAndMatchedApplications(RegisteredUser user,//
			SearchCategory searchCategory, String term,//
			SortCategory sortCategory, SortOrder sortOrder, int blockCount) {

		List<ApplicationForm> matchingApplications = new ArrayList<ApplicationForm>();
		List<ApplicationForm> visibleApplications = applicationFormDAO.getVisibleApplications(user);

		if (searchCategory == null) {
			matchingApplications = visibleApplications;
		} else {
			if (term == null) {
				throw new IllegalArgumentException("Search term cannot be null when a search-category is set!");
			}
			for (ApplicationForm applicationForm : visibleApplications) {
				if (searchCategory == SearchCategory.APPLICATION_NUMBER) {
					if (applicationForm.getApplicationNumber().toLowerCase().contains(term.toLowerCase())) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.PROGRAMME_NAME) {
					String fullProgramName = applicationForm.getProgram().getCode() + applicationForm.getProgram().getTitle();
					if (fullProgramName.toLowerCase().contains(term.toLowerCase())) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.APPLICANT_NAME) {
					String fullApplicantName = applicationForm.getApplicant().getFirstName() + applicationForm.getApplicant().getLastName();
					if (fullApplicantName.toLowerCase().contains(term.toLowerCase())) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.APPLICATION_STATUS) {
					ApplicationFormStatus matchedStatus = null;
					for (ApplicationFormStatus status : ApplicationFormStatus.values()) {
						if (status.displayValue().toLowerCase().contains(term.toLowerCase())) {
							matchedStatus = status;
						}
					}
					if (matchedStatus != null) {
						if (applicationForm.getStatus() == matchedStatus) {
							matchingApplications.add(applicationForm);
						}
					}
				}
			}
		}
		matchingApplications = sortApplicationList(matchingApplications, sortCategory, sortOrder);
		matchingApplications = reduceApplicationListSize(matchingApplications, blockCount);
		return matchingApplications;
	}

	private List<ApplicationForm> sortApplicationList(List<ApplicationForm> applications, SortCategory sortCategory, SortOrder sortOrder) {
		if (sortCategory == null) {// natural ordering on submitted date.
			Collections.sort(applications);
			return applications;
		}
		if (sortOrder == null) {
			throw new IllegalArgumentException("Sort order cannot be null when a sort-category is set!");
		}
		Collections.sort(applications, sortCategory.getComparator(sortOrder));
		return applications;
	}

	private List<ApplicationForm> reduceApplicationListSize(List<ApplicationForm> applications, int blockCount) {
		if (blockCount < 1) {
			throw new IllegalArgumentException("Number of application blocks must be greater than 0!");
		}
		int toIndex = blockCount * APPLICATION_BLOCK_SIZE;
		if (toIndex > applications.size()) {
			return applications;
		}
		return applications.subList(0, toIndex);
	}

	@Transactional
	public List<ApplicationForm> getApplicationsDueRegistryNotification() {
		return applicationFormDAO.getApplicationsDueRegistryNotification();
	}
}