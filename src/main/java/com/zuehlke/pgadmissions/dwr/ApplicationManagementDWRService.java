package com.zuehlke.pgadmissions.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.HibernateFlusher;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@RemoteProxy(name = "applicationDWR")
@Component
public class ApplicationManagementDWRService {

	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final HibernateFlusher hibernateFlusher;

	ApplicationManagementDWRService() {
		this(null, null, null);
	}

	@Autowired
	public ApplicationManagementDWRService(ApplicationsService applicationsService, UserService userService, HibernateFlusher hibernateFlusher) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		
		this.hibernateFlusher = hibernateFlusher;

	}

	@RemoteMethod
	public String acceptApplication(Integer applicationId) {

		RegisteredUser approver = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(!approver.isInRole(Authority.APPROVER)){
			throw new AccessDeniedException();
		}
		if(applicationId == null){
			throw new IllegalArgumentException("Application ID must be provided");
		}
		ApplicationForm applicationForm = applicationsService.getApplicationById(applicationId);
		if(applicationForm == null){
			throw new ResourceNotFoundException("No such application form exists");
		}
		if(!applicationForm.getProject().getProgram().isApprover(approver)){
			throw new AccessDeniedException();
		}
		if(applicationForm.getApprovalStatus() != null){
			throw new IllegalArgumentException("Application has already been accepted or rejected");
		}
		applicationForm.setApprover(approver);
		applicationForm.setApprovalStatus(ApprovalStatus.APPROVED);
		hibernateFlusher.flush();
		return null;
	}
}
