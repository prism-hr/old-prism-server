package com.zuehlke.pgadmissions.dwr;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.dwr.models.PersonalDetailsDWR;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;


@RemoteProxy(name = "acceptDWR")
@Component
public class ApplicationManagementDWRService {

	private final ApplicationsService applicationsService;
	private final UserService userService;

	ApplicationManagementDWRService() {
		this(null, null);
	}

	@Autowired
	public ApplicationManagementDWRService(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		
	}

	@RemoteMethod
	public String acceptApplication(Integer appId) {
 
		System.out.println("I am in dwr w. appId " +  appId);
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

	@RemoteMethod
	public PersonalDetailsDWR displayPersonalDetails(Integer userId){
		RegisteredUser registeredUser = userService.getUser(userId);
		
		PersonalDetailsDWR user = new PersonalDetailsDWR();
		
		user.setFirstName(registeredUser.getFirstName());
		user.setLastName(registeredUser.getLastName());

		return user;
		
	}
}
