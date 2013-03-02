package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class SupervisorPropertyEditor extends PropertyEditorSupport {

	private final UserService userService;
	private final ApplicationsService applicationsService;
	private final EncryptionHelper encryptionHelper;

	public SupervisorPropertyEditor() {
		this(null, null, null);
	}

	@Autowired
	public SupervisorPropertyEditor(UserService userService, ApplicationsService applicationsService,//
			EncryptionHelper encryptionHelper) {
		this.userService = userService;
		this.applicationsService = applicationsService;
		this.encryptionHelper = encryptionHelper;
	}

	@Override
	public String getAsText() {
		return null;
	}

	@Override
	public void setAsText(String strAppAndUserId) throws IllegalArgumentException {
		if (StringUtils.isBlank(strAppAndUserId)) {
			setValue(null);
			return;
		}
		String[] split = strAppAndUserId.split("\\|");
		if (split.length < 2 || split.length > 3) {
			throw new IllegalArgumentException();
		}
		String appId = split[0];
		Integer userId = encryptionHelper.decryptToInteger(split[1]);
		RegisteredUser user = userService.getUser(userId);
		if (user == null) {
			throw new IllegalArgumentException("no such user: " + split[1]);
		}

		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(appId);
		if (applicationForm == null) {
			throw new IllegalArgumentException("no such applications: " + split[0]);
		}

		Supervisor supervisor = findCreateNewSupervisor(user);
		if(split.length == 3 && "primary".equals(split[2])){
		    supervisor.setIsPrimary(true);
		}
		
        setValue(supervisor);
	}

	private Supervisor findCreateNewSupervisor(RegisteredUser user) {

		Supervisor supervisor = new Supervisor();		
		supervisor.setUser(user);
		return supervisor;
	}
}
