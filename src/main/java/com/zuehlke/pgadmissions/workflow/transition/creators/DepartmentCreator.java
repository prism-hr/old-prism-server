package com.zuehlke.pgadmissions.workflow.transition.creators;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class DepartmentCreator implements ResourceCreator<DepartmentDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, DepartmentDTO newResource) {
        Institution institution = resourceCreatorUtils.getParentResource(newResource);

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(institution, advertDTO, newResource.getName(), user);

        Department department = new Department().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(institution)
                .withAdvert(advert).withName(advert.getName());

        departmentService.setImportedPrograms(department, newResource.getImportedPrograms());
        resourceService.setResourceAttributes(department, newResource);
        String opportunityCategories = newResource.getOpportunityCategories().stream().map(c -> c.name()).collect(Collectors.joining("|"));
        resourceService.setOpportunityCategories(department, opportunityCategories);
        return department;
    }

}
