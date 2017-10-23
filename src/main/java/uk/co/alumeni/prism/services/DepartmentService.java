package uk.co.alumeni.prism.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.resource.Department;

import javax.inject.Inject;

@Service
@Transactional
public class DepartmentService {

    @Inject
    private EntityService entityService;

    public Department getById(Integer id) {
        return entityService.getById(Department.class, id);
    }

}
