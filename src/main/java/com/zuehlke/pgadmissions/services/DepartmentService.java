package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.department.Department;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private EntityService entityService;
    
    public Department getOrCreateDepartment(Department department) {
        return entityService.getOrCreate(department);
    }
    
}
