package com.zuehlke.pgadmissions.services;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private EntityService entityService;
    
    @Inject
    private InstitutionService institutionService;
    
    public Department getById(Integer id) {
        return entityService.getById(Department.class, id);
    }
    
    public Department getOrCreateDepartment(Department department) {
        return entityService.getOrCreate(department);
    }
    
    public Department getOrCreateDepartment(DepartmentDTO departmentDTO) {
        Integer departmentId = departmentDTO.getId();
        if (departmentId == null) {
            Institution institution = institutionService.getById(departmentDTO.getInstitutionId());
            return getOrCreateDepartment(new Department().withInstitution(institution).withTitle(departmentDTO.getTitle()));
        }
        return getById(departmentId);
    }
    
}
