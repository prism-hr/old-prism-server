package com.zuehlke.pgadmissions.workflow.selectors;

import java.util.List;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

@Component
public class RefereeSelector implements PrismSelector<User> {

	@Override
	public List<User> getSelected(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getPromoted(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getPossible(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

}
