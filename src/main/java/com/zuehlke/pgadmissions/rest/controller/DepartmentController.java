package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.services.DepartmentService;

@RestController
@RequestMapping("api/departments/{departmentId}")
@PreAuthorize("isAuthenticated()")
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @RequestMapping(value = "/importedPrograms", method = RequestMethod.PUT)
    public void updateImportedPrograms(@PathVariable Integer departmentId,
            @Valid @RequestBody List<ImportedEntityDTO> importedPrograms) throws Exception {
        departmentService.updateImportedPrograms(departmentId, importedPrograms);
    }

}
