package uk.co.alumeni.prism.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.services.DepartmentService;

@Component
public class DepartmentBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private DepartmentService departmentService;

    @Override
    public void persist(Integer departmentId, Document image) {
        Department department = departmentService.getById(departmentId);
        department.getAdvert().setBackgroundImage(image);
    }

}
