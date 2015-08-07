package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
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

    public List<Department> getDepartments(String searchTerm) {
        return departmentDAO.getDepartments(searchTerm);
    }

    public void setImportedPrograms(Department department, List<ImportedEntityDTO> importedProgramDTOs) {
        Set<ImportedProgram> importedPrograms = department.getImportedPrograms();
        for (ImportedEntityDTO importedProgramDTO : importedProgramDTOs) {
            importedPrograms.add(importedEntityService.getById(ImportedProgram.class, importedProgramDTO.getId()));
        }
    }

}
