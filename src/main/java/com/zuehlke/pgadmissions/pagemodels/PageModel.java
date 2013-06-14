package com.zuehlke.pgadmissions.pagemodels;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

@Deprecated
public class PageModel {

	private List<ObjectError> errorObjs;
	private ApplicationForm applicationForm;
	private RegisteredUser user;
	private List<Comment> applicationComments = new ArrayList<Comment>();
	private String view;
	private BindingResult result;
	private List<String> globalErrorCodes = new ArrayList<String>();
	private String userRoles;

	public BindingResult getResult() {
		return result;
	}

	private void setUserRoles(final RegisteredUser user) {
	    StringBuilder userRolesStrBuilder = new StringBuilder();
	    if (user != null) {
	        ArrayList<Role> authorities = new ArrayList<Role>(user.getAuthorities());
			for (Role role : authorities) {
				userRolesStrBuilder.append(role.getAuthority());
				userRolesStrBuilder.append(";");
			}
		}
		userRoles = userRolesStrBuilder.toString();
	}

	public String getUserRoles() {
		return userRoles;
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

	public List<Comment> getApplicationComments() {
		return applicationComments;
	}

	public void setApplicationComments(List<Comment> applicationComments) {
		this.applicationComments = applicationComments;
	}

	public void setView(String view) {
		this.view = view;

	}

	public boolean hasError(String fieldname) {
		if (result != null && result.getFieldError(fieldname) != null) {
			return true;
		}
		return false;
	}

	public List<String> getGlobalErrorCodes() {
		return globalErrorCodes;
	}

	public void setGlobalErrorCodes(List<String> globalErrorCodes) {
		this.globalErrorCodes = globalErrorCodes;
	}
}
