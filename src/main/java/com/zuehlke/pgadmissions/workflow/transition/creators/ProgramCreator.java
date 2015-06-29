package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProgramCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        ResourceOpportunityDTO newProgram = (ResourceOpportunityDTO) newResource;
        Institution institution = institutionService.getById(newProgram.getResourceId());

        AdvertDTO advertDTO = newProgram.getAdvert();
        Advert advert = advertService.createAdvert(institution, advertDTO);

        DepartmentDTO departmentDTO = newProgram.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(institution, departmentDTO);

        Program program = new Program().withUser(user).withParentResource(institution).withDepartment(department).withAdvert(advert)
                .withTitle(advert.getTitle()).withDurationMinimum(newProgram.getDurationMinimum()).withDurationMaximum(newProgram.getDurationMaximum())
                .withRequireProjectDefinition(false);

        resourceService.setResourceAttributes(program, newProgram);
        return program;
    }

}
