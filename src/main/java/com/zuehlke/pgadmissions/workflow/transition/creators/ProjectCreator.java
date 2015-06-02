package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

@Component
public class ProjectCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        OpportunityDTO newProject = (OpportunityDTO) newResource;

        PrismScope resourceScope = newProject.getResourceScope();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, newProject.getResourceId());

        AdvertDTO advertDTO = newProject.getAdvert();
        Advert advert = advertService.createAdvert(resource, advertDTO);

        DepartmentDTO departmentDTO = newProject.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(resource.getInstitution(), departmentDTO);

        Project project = new Project().withUser(user).withParentResource(resource).withDepartment(department).withAdvert(advert)
                .withTitle(advert.getTitle()).withDurationMinimum(newProject.getDurationMinimum()).withDurationMaximum(newProject.getDurationMaximum())
                .withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD));
        
        resourceService.updatePartner(user, project, newProject);
        resourceService.adoptPartnerAddress(project, advert);
        resourceService.setResourceAttributes(project, newProject);
        
        return project;
    }

}
