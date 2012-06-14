package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;
@Component
public class InterviewerPropertyEditor extends PropertyEditorSupport {

	private final UserService userService;

	InterviewerPropertyEditor(){
		this(null);
	}
	@Autowired
	public InterviewerPropertyEditor(UserService userService) {
		this.userService = userService;
	}

	@Override
	public String getAsText() {
		return null;
	}

	@Override
	public void setAsText(String strUserId) throws IllegalArgumentException {
		if (StringUtils.isBlank(strUserId)) {
			setValue(null);
			return;
		}
		Integer userId = Integer.parseInt(strUserId);
		RegisteredUser user = userService.getUser(userId);
		if(user == null){
			throw new IllegalArgumentException("no such user: " + strUserId);
		}
		Interviewer interviewer = new Interviewer();
		interviewer.setUser(user);
		interviewer.setFirstAdminNotification(true);
		setValue(interviewer);
	}

}
