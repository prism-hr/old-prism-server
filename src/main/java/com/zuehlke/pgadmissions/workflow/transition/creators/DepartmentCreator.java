package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class DepartmentCreator implements ResourceCreator<ResourceParentDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, ResourceParentDTO newResource) {
        Institution institution = resourceCreatorUtils.getParentResource(newResource);

        if (newResource.getAdvert() == null) {
            newResource.setAdvert(new AdvertDTO());
        }

        AdvertDTO advertDTO = newResource.getAdvert();
        advertDTO.setGloballyVisible(DEPARTMENT.isDefaultShared());
        Advert advert = advertService.createAdvert(institution, advertDTO, newResource.getName(), user);

        Department department = new Department().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(institution).withAdvert(advert)
                .withName(advert.getName());

        List<PrismOpportunityCategory> opportunityCategories = newResource.getOpportunityCategories();
        newResource.setOpportunityCategories(isEmpty(opportunityCategories)
                ? asList(institution.getOpportunityCategories().split("\\|")).stream().map(PrismOpportunityCategory::valueOf).collect(toList()) : opportunityCategories);

        resourceService.setResourceAttributes(department, newResource);
        return department;
    }

}
