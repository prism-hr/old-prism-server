package com.zuehlke.pgadmissions.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Service
@RemoteProxy(name = "acceptDWR")
public class ApplicationManagementDWRService {

	private final ApplicationsService applicationsService;

	ApplicationManagementDWRService() {
		this(null);
	}

	@Autowired
	public ApplicationManagementDWRService(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RemoteMethod
	public String acceptApplication(Integer appId) {

		System.out.println("I am in dwr!!!");
		String status = "";

		ApplicationForm application = applicationsService.getApplicationById(appId);
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser approver = (RegisteredUser) context.getAuthentication().getDetails();

		if (application.getApprovalStatus() == null) {
			application.setApprovalStatus(ApprovalStatus.APPROVED);
			application.setApprover(approver);
			applicationsService.save(application);
			status = "success";
		} else {
			status = "failure";
		}

		return status;
	}

}
