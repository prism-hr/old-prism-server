package uk.co.alumeni.prism.workflow.evaluators;

import uk.co.alumeni.prism.domain.resource.Resource;

public interface ResourceCompletenessEvaluator<T extends Resource> {

    boolean evaluate(T resource);

}
