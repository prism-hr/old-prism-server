package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentInvitationDTO;
import com.zuehlke.pgadmissions.services.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/departments")
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{departmentId}/importedPrograms", method = RequestMethod.PUT)
    public void updateImportedPrograms(@PathVariable Integer departmentId,
            @Valid @RequestBody List<ImportedEntityDTO> importedPrograms) {
        departmentService.updateImportedPrograms(departmentId, importedPrograms);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public void inviteDepartment(@Valid @RequestBody DepartmentInvitationDTO departmentInvitationDTO){
        departmentService.inviteDepartment(departmentInvitationDTO);
    }

}
