package com.zuehlke.pgadmissions.workflow.recommenders.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

@Component
public class RefereeRecommender implements UserRecommender {

	@Override
    public List<User> getPreselected(Resource resource) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public List<User> getPromoted(Resource resource) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public List<User> getPotential(Resource resource) {
	    // TODO Auto-generated method stub
	    return null;
    }

}
