package uk.co.alumeni.prism.workflow.transition.populators;

import uk.co.alumeni.prism.domain.resource.Resource;

public interface ResourcePopulator<T extends Resource> {

    void populate(T resource);

}
