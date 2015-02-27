package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.user.User;


public abstract class ApplicationAssignmentSection extends ApplicationSection implements IUniqueEntity {

	public abstract Integer getId();
	
	public abstract void setId(Integer id);
	
	public abstract Application getApplication();
	
	public abstract void setApplication(Application application);
	
	public abstract User getUser();

	public abstract void setUser(User user);
	
	@Override
	public ResourceSignature getResourceSignature() {
		return new ResourceSignature().addProperty("application", getApplication()).addProperty("user", getUser());
	}

}
