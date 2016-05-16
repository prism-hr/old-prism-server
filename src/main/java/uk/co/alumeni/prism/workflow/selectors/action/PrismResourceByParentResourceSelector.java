package uk.co.alumeni.prism.workflow.selectors.action;

import java.util.List;

import uk.co.alumeni.prism.domain.resource.Resource;

public interface PrismResourceByParentResourceSelector {

    List<Integer> getPossible(Resource parentResource);

}
