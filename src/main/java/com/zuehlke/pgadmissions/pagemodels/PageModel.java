package com.zuehlke.pgadmissions.pagemodels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class PageModel {

	private List<ObjectError> errorObjs;
	private ApplicationForm applicationForm;
	private RegisteredUser user;
	private List<ApplicationReview> applicationComments;
	private String view;
	private BindingResult result;
	private String userRoles;
	private List<String> globalErrorCodes = new ArrayList<String>();

	public BindingResult getResult() {
		return result;
	}

	public void setResult(BindingResult result) {
		this.result = result;
	}

	public String getView() {
		return view;
	}

	public List<ObjectError> getErrorObjs() {
		return errorObjs;
	}

	public void setErrorObjs(List<ObjectError> errorObjs) {
		this.errorObjs = errorObjs;
	}

	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
		setUserRoles(user);
	}

	public List<ApplicationReview> getApplicationComments() {
		return applicationComments;
	}

	public void setApplicationComments(List<ApplicationReview> applicationComments) {
		this.applicationComments = applicationComments;
	}

	public void setView(String view) {
		this.view = view;

	}

	public boolean hasError(String fieldname){
		if(result != null && result.getFieldError(fieldname) != null){
			return true;
		}
		return false;
	}

	private void setUserRoles(RegisteredUser user) {
		StringBuilder userRoles = new StringBuilder();
		Collection<Role> authorities = user.getAuthorities();
		if (user != null && authorities!= null) {
			for (Role role : authorities) {
				userRoles.append(role.getAuthority());
				userRoles.append(";");
			}
		}
		this.userRoles = userRoles.toString();
	}

	public String getUserRoles() {
		return userRoles;
	}

	public List<String> getGlobalErrorCodes() {
		return globalErrorCodes;
	}

	public void setGlobalErrorCodes(List<String> globalErrorCodes) {
		this.globalErrorCodes = globalErrorCodes;
	}
}
