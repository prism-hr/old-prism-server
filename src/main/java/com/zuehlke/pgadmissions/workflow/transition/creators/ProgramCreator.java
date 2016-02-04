package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProgramCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        OpportunityDTO newProgram = (OpportunityDTO) newResource;
        Institution institution = institutionService.getById(newProgram.getResourceId());

        AdvertDTO advertDTO = newProgram.getAdvert();
        Advert advert = advertService.createAdvert(institution, advertDTO);

        DepartmentDTO departmentDTO = newProgram.getDepartment();
        Department department = departmentDTO == null ? null : departmentService.getOrCreateDepartment(institution, departmentDTO);
        OpportunityType opportunityType = importedEntityService.getByCode(OpportunityType.class, institution, newProgram.getOpportunityType().name());

        Program program = new Program().withUser(user).withParentResource(institution).withDepartment(department).withAdvert(advert)
                .withOpportunityType(opportunityType).withTitle(advert.getTitle()).withDurationMinimum(newProgram.getDurationMinimum())
                .withDurationMaximum(newProgram.getDurationMaximum()).withRequireProjectDefinition(false)
                .withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD)).withImported(false);
        
        resourceService.updatePartner(user, program, newProgram);
        resourceService.adoptPartnerAddress(program, advert);
        resourceService.setResourceAttributes(program, newProgram);
        
        return program;
    }

}
