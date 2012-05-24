package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Component
public class RolePropertyEditor extends PropertyEditorSupport{

		private final RoleDAO roleDAO;

		RolePropertyEditor(){
			this(null);
		}
		@Autowired
		public RolePropertyEditor(RoleDAO roleDAO) {
			this.roleDAO = roleDAO;
		 
		}
		
		@Override
		public void setAsText(String authority) throws IllegalArgumentException{
			Authority authorityEnum = Authority.valueOf(authority);
			if(authorityEnum == null ){
				setValue(null);
				return;
			}
			Role role = roleDAO.getRoleByAuthority(authorityEnum);
			setValue(role);
			
		}
		
		@Override
		public String getAsText() {
			if(getValue() == null|| ((Role)getValue()).getAuthority() == null){
				return null;
			}
			return ((Role)getValue()).getAuthority().toString();
		}
	}

