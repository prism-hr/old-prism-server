package com.zuehlke.pgadmissions.workflow.evaluators;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourceCompletenessEvaluator<T extends Resource> {

    boolean evaluate(T resource);

}
