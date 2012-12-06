package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Service("applicationsService")
public class ApplicationsService {
	
    private final int APPLICATION_BLOCK_SIZE = 50;
    
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

	@Transactional
    public List<ApplicationForm> getAllVisibleAndMatchedApplications(RegisteredUser user,
            SearchCategory searchCategory,
            String term, 
            SortCategory sort, 
            SortOrder order,
            Integer blockCount) {

	    int pageCount = blockCount == null ? 1 : blockCount;
        SortCategory sortCategory = sort == null ? SortCategory.DEFAULT : sort;
        SortOrder sortOrder = order == null ? SortOrder.ASCENDING : order;
        
        if (pageCount < 0) {
            pageCount = 0;
        }
	    
		List<ApplicationForm> matchingApplications = new ArrayList<ApplicationForm>();
		List<ApplicationForm> visibleApplications = applicationFormDAO.getVisibleApplications(user, sortCategory, sortOrder, pageCount, APPLICATION_BLOCK_SIZE);

		if (searchCategory == null) {
			matchingApplications = visibleApplications;
		} else {
			if (term == null) {
				throw new IllegalArgumentException("Search term cannot be null when a search-category is set!");
			}
			for (ApplicationForm applicationForm : visibleApplications) {
				if (searchCategory == SearchCategory.APPLICATION_NUMBER) {
					if (StringUtils.containsIgnoreCase(applicationForm.getApplicationNumber(), term)) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.PROGRAMME_NAME) {
					String fullProgramName = applicationForm.getProgram().getCode() + applicationForm.getProgram().getTitle();
					if (StringUtils.containsIgnoreCase(fullProgramName, term)) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.APPLICANT_NAME) {
					String fullApplicantName = applicationForm.getApplicant().getFirstName() + applicationForm.getApplicant().getLastName();
					if (StringUtils.containsIgnoreCase(fullApplicantName, term)) {
						matchingApplications.add(applicationForm);
					}
				}
				if (searchCategory == SearchCategory.APPLICATION_STATUS) {
					for (ApplicationFormStatus status : ApplicationFormStatus.values()) {
						if (StringUtils.containsIgnoreCase(status.displayValue(), term)) {
							if (applicationForm.getStatus() == status) {
    							matchingApplications.add(applicationForm);
    							break;
    						}
    					}
					}
				}
			}
		}
	
//		if (sortCategory == SortCategory.DEFAULT) {
//		    Collections.sort(matchingApplications);
//		}
	
		return matchingApplications;
	}

	@Transactional
	public List<ApplicationForm> getApplicationsDueRegistryNotification() {
		return applicationFormDAO.getApplicationsDueRegistryNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestNotification() {
		return applicationFormDAO.getApplicationsDueApprovalRequestNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestReminder() {
		return applicationFormDAO.getApplicationDueApprovalRestartRequestReminder();
	}

	
}