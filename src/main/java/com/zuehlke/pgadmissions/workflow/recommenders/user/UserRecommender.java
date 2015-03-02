package com.zuehlke.pgadmissions.workflow.recommenders.user;

import java.util.List;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public interface UserRecommender {

	public List<User> getPreselected(Resource resource);
	
	public List<User> getPromoted(Resource resource);
	
	public List<User> getPotential(Resource resource);
	
}
