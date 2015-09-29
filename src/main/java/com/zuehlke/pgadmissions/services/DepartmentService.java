package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DepartmentDAO;
import com.zuehlke.pgadmissions.domain.resource.Department;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private DepartmentDAO departmentDAO;

    @Inject
    private EntityService entityService;

    public Department getById(Integer id) {
        return entityService.getById(Department.class, id);
    }

    public List<Integer> getDepartments(Integer institution) {
        return departmentDAO.getDepartments(institution);
    }

}
