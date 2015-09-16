package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertTargetResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertTargetsDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private DepartmentDAO departmentDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private EntityService entityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    public Department getById(Integer id) {
        return entityService.getById(Department.class, id);
    }

    public List<Integer> getDepartments(Integer institution) {
        return departmentDAO.getDepartments(institution);
    }

    public ActionOutcomeDTO inviteDepartment(DepartmentInvitationDTO departmentInvitationDTO) {
        ResourceParentDivisionDTO departmentDTO = departmentInvitationDTO.getDepartment();

        List<PrismOpportunityCategory> opportunityCategories = Lists.newArrayList();
        Institution institution = institutionService.getById(departmentDTO.getParentResource().getId());
        String opportunityCategoriesString = institution.getOpportunityCategories();
        if (opportunityCategoriesString != null) {
            opportunityCategories = Stream.of(institution.getOpportunityCategories().split("\\|"))
                    .map(c -> PrismOpportunityCategory.valueOf(c))
                    .collect(Collectors.toList());
        }

        AdvertDTO advertDTO = new AdvertDTO();
        departmentDTO.setAdvert(advertDTO);
        departmentDTO.setOpportunityCategories(opportunityCategories);

        Department department = null;
        UserSimpleDTO departmentUser = null;
        ActionOutcomeDTO outcome = null;
        if (institution != null) {
            outcome = resourceService.createResource(institution.getUser(), actionService.getById(INSTITUTION_CREATE_DEPARTMENT), departmentDTO);
            if (outcome != null) {
                department = (Department) outcome.getResource();
                departmentUser = departmentInvitationDTO.getDepartmentUser();
                if (departmentUser != null) {
                    userService.requestUser(departmentUser, department, DEPARTMENT_ADMINISTRATOR);
                }
            }
        }

        if (department != null) {
            Integer advertId = departmentInvitationDTO.getAdvertId();
            if (advertId != null) {
                Advert advert = advertService.getById(advertId);
                if (advert != null) {
                    AdvertTargetResourceDTO targetDTO = new AdvertTargetResourceDTO().withScope(DEPARTMENT).withId(department.getId()).withUser(departmentUser);
                    advertService.updateTargets(advert, new AdvertTargetsDTO().withSelectedResources(newArrayList(targetDTO)), false);
                }
            }
        }

        return outcome;
    }

}
