package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.services.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

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
