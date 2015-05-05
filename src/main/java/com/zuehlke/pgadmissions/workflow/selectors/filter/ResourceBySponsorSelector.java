package com.zuehlke.pgadmissions.workflow.selectors.filter;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ResourceBySponsorSelector implements PrismResourceListFilterSelector<Integer> {

    @Inject
    private ResourceService resourceService;
    
    @Override
    public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
        return resourceService.getResourcesByPartner(scope, constraint.getValueString());
    }

}
