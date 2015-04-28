package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceParentDTO.ResourceParentAttributesDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProjectCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;
    
    @Inject
    private DepartmentService departmentService;
    
    @Inject
    private ImportedEntityService importedEntityService;
    
    @Inject
    private ResourceService resourceService;
    
    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        OpportunityDTO newProject = (OpportunityDTO) newResource;
        
        PrismScope resourceScope = newProject.getResourceScope();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, newProject.getResourceId());

        AdvertDTO advertDTO = newProject.getAdvert();
        Advert advert = advertService.createAdvert(user, advertDTO);

        DepartmentDTO departmentDTO = newProject.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(departmentDTO);

        Program program = null;
        boolean imported = false;
        if (resourceScope == PROGRAM) {
            program = (Program) resource;
            imported = BooleanUtils.isTrue(program.getImported());
        }

        OpportunityType opportunityType;
        if (imported) {
            opportunityType = program.getOpportunityType();
        } else {
            opportunityType = importedEntityService.getByCode(OpportunityType.class, resource.getInstitution(), newProject.getOpportunityType().name());
        }

        Project project = new Project().withUser(user).withResource(resource).withDepartment(department).withAdvert(advert)
                .withOpportunityType(opportunityType).withTitle(advert.getTitle()).withDurationMinimum(newProject.getDurationMinimum())
                .withDurationMaximum(newProject.getDurationMaximum()).withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD));
        
        ResourceParentAttributesDTO attributes = newProject.getAttributes();
        resourceService.setResourceConditions(project, attributes.getConditions());

        if (!imported) {
            resourceService.setStudyOptions(project, attributes.getStudyOptions(), new LocalDate());
        }
        
        resourceService.setStudyLocations(project, attributes.getStudyLocations());
        return project;
    }

}
