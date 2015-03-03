package com.zuehlke.pgadmissions.workflow.recommenders.state;

import java.util.List;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;

public interface StateRecommender {

	public List<State> getPreselected(Resource resource);
	
	public List<State> getPromoted(Resource resource);
	
	public List<State> getPotential(Resource resource);

}
