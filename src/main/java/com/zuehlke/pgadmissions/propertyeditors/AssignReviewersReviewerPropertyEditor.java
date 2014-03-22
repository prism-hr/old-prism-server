package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class AssignReviewersReviewerPropertyEditor extends PropertyEditorSupport {

	private final UserService userService;
	private final ApplicationFormService applicationsService;
	private final EncryptionHelper encryptionHelper;

	public AssignReviewersReviewerPropertyEditor() {
		this(null, null, null);
	}

	@Autowired
	public AssignReviewersReviewerPropertyEditor(UserService userService, ApplicationFormService applicationsService, EncryptionHelper encryptionHelper) {
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
		if (split.length != 2) {
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

		setValue(findExistingOrCreateNewReviewer(user, applicationForm));
	}

	private Reviewer findExistingOrCreateNewReviewer(RegisteredUser user, ApplicationForm applicationForm) {
		if (applicationForm.getLatestReviewRound() != null) {
			for (Reviewer existingReviewer : applicationForm.getLatestReviewRound().getReviewers()) {
				if (user.getId().equals(existingReviewer.getUser().getId())) {
					return existingReviewer;
				}
			}
		}
		Reviewer reviewer = new Reviewer();
		reviewer.setUser(user);
		return reviewer;
	}
}
