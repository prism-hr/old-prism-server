package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private DepartmentDAO departmentDAO;

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

    public Department getOrCreateDepartment(Institution institution, DepartmentDTO departmentDTO) {
        Integer departmentId = departmentDTO.getId();
        if (departmentId == null) {
            return getOrCreateDepartment(new Department().withInstitution(institution).withTitle(departmentDTO.getTitle()));
        }
        return getById(departmentId);
    }

    public List<ResourceRepresentationSimple> getDepartments(Integer institutionId) {
        Institution institution = institutionService.getById(institutionId);
        return departmentDAO.getDepartments(institution);
    }

}
