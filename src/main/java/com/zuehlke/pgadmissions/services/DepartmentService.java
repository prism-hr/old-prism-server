package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareDecimalForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareRowsForSqlInsert;
import static java.math.RoundingMode.HALF_UP;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.DepartmentImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private DepartmentDAO departmentDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    public Department getById(Integer id) {
        return entityService.getById(Department.class, id);
    }

    public void setImportedPrograms(Department department, List<ImportedEntityDTO> importedProgramDTOs) {
        Set<ImportedProgram> importedPrograms = department.getImportedPrograms();
        importedPrograms.clear();
        if (importedProgramDTOs != null) {
            for (ImportedEntityDTO importedProgramDTO : importedProgramDTOs) {
                importedPrograms.add(importedEntityService.getById(ImportedProgram.class, importedProgramDTO.getId()));
            }
        }
    }

    public void synchronizeImportedSubjectAreas(Department department) {
        departmentDAO.deleteDepartmentImportedSubjectAreas(department);

        List<DepartmentImportedSubjectAreaDTO> subjectAreas = departmentDAO.getImportedSubjectAreas(department);
        if (!subjectAreas.isEmpty()) {
            List<String> rows = Lists.newArrayList();
            for (DepartmentImportedSubjectAreaDTO subjectAreaDTO : departmentDAO.getImportedSubjectAreas(department)) {
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

}
