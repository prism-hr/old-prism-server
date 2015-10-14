package com.zuehlke.pgadmissions.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.services.DepartmentService;

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
