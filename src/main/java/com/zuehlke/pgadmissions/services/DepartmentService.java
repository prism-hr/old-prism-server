package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.DepartmentImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetRelevanceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.DEPARTMENT_COMMENT_UPDATED_IMPORTED_PROGRAMS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private DepartmentDAO departmentDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

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

    public void inviteDepartment(DepartmentInvitationDTO departmentInvitationDTO) {
        DepartmentDTO departmentDTO = departmentInvitationDTO.getDepartment();

        Institution institution = institutionService.getById(departmentDTO.getParentResource().getId());
        List<PrismOpportunityCategory> opportunityCategories = Stream.of(institution.getOpportunityCategories().split("\\|"))
                .map(c -> PrismOpportunityCategory.valueOf(c))
                .collect(Collectors.toList());

        AdvertDTO advertDTO = new AdvertDTO();
        departmentDTO.setAdvert(advertDTO);
        departmentDTO.setOpportunityCategories(opportunityCategories);

        if (institution != null) {
            ActionOutcomeDTO outcome = resourceService.createResource(institution.getUser(), actionService.getById(INSTITUTION_CREATE_DEPARTMENT), departmentDTO);
            if (outcome != null) {
                UserDTO user = departmentInvitationDTO.getDepartmentUser();
                if (user != null) {
                    userService.getOrCreateUserWithRoles(institution.getUser(), user.getFirstName(), user.getLastName(), user.getEmail(), outcome.getResource(),
                            asList(DEPARTMENT_ADMINISTRATOR));
                }
            }
        }
    }

    public void setImportedPrograms(Department department, List<ImportedEntityDTO> importedProgramDTOs) {
        Set<ImportedProgram> importedPrograms = department.getImportedPrograms();
        importedPrograms.clear();
        if (importedProgramDTOs != null) {
            importedProgramDTOs.forEach(importedProgramDTO -> {
                ImportedProgram importedProgram = importedEntityService.getById(ImportedProgram.class, importedProgramDTO.getId());
                if (importedProgram.getInstitution().getId().equals(department.getInstitution().getImportedInstitution().getId())) {
                    importedPrograms.add(importedProgram);
                }
            });
        }
    }

    public void updateImportedPrograms(Integer departmentId, List<ImportedEntityDTO> importedPrograms) {
        Department department = getById(departmentId);
        setImportedPrograms(department, importedPrograms);
        resourceService.executeUpdate(department, DEPARTMENT_COMMENT_UPDATED_IMPORTED_PROGRAMS);
    }

    public void synchronizeImportedSubjectAreas(Department department) {
        departmentDAO.deleteDepartmentImportedSubjectAreas(department);
        entityService.flush();

        List<DepartmentImportedSubjectAreaDTO> subjectAreas = departmentDAO.getImportedSubjectAreas(department);
        if (!subjectAreas.isEmpty()) {
            List<String> rows = Lists.newArrayList();
            for (DepartmentImportedSubjectAreaDTO subjectAreaDTO : subjectAreas) {
                List<String> columns = Lists.newLinkedList();
                columns.add(prepareIntegerForSqlInsert(subjectAreaDTO.getDepartment()));
                columns.add(prepareIntegerForSqlInsert(subjectAreaDTO.getSubjectArea()));
                columns.add(prepareDecimalForSqlInsert(subjectAreaDTO.getProgramRelationStrength().multiply(
                        subjectAreaDTO.getInstitutionRelationStrength()).setScale(RATING_PRECISION, HALF_UP)));
                rows.add(prepareColumnsForSqlInsert(columns));
            }

            entityService.executeBulkInsert("department_imported_subject_area", "department_id, imported_subject_area_id, relation_strength",
                    prepareRowsForSqlInsert(rows));
        }
    }

    public List<Department> getDepartmentsByImportedProgram(ImportedProgram importedProgram) {
        return departmentDAO.getDepartmentsByImportedProgram(importedProgram);
    }

    public List<ResourceTargetRelevanceDTO> getDepartmentsBySubjectAreas(Integer institution, Collection<Integer> subjectAreas) {
        return departmentDAO.getDepartmentsBySubjectAreas(institution, subjectAreas);
    }

}
