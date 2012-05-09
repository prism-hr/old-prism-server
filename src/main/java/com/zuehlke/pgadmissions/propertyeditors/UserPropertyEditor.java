package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserPropertyEditor extends PropertyEditorSupport {

	private final UserService userService;
	
	UserPropertyEditor() {
		this(null);
	
	}
	@Autowired
	public UserPropertyEditor(UserService userService) {
		this.userService = userService;
	
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null){
			setValue(null);
			return;
		}
		setValue(userService.getUser(Integer.parseInt(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((RegisteredUser)getValue()).getId() == null){
			return null;
		}
		return ((RegisteredUser)getValue()).getId().toString();
	}

}
