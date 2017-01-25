package uk.co.alumeni.prism.workflow.selectors.action;

import uk.co.alumeni.prism.domain.resource.Resource;

import java.util.List;

public interface PrismResourceByParentResourceSelector {

    List<Integer> getPossible(Resource parentResource);

}
