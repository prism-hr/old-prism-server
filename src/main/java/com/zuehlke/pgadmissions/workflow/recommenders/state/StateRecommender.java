package com.zuehlke.pgadmissions.workflow.recommenders.state;

import java.util.List;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;

public interface StateRecommender {

	public List<State> getRecommendations(Resource resource);

}
