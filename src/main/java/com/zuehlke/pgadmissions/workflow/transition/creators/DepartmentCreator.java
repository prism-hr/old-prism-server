package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class DepartmentCreator implements ResourceCreator<ResourceParentDivisionDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, ResourceParentDivisionDTO newResource) throws Exception {
        ResourceParent parentResource = resourceCreatorUtils.getParentResource(user, newResource);

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(parentResource, advertDTO);

        Department department = new Department().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(parentResource)
                .withAdvert(advert).withName(advert.getName());

        resourceService.setResourceAttributes(department, newResource);
        return department;
    }

}
