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

import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.mapping.ActionMapper;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.DepartmentInvitationDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.services.DepartmentService;

@RestController
@RequestMapping("api/departments")
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ActionMapper actionMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{departmentId}/importedPrograms", method = RequestMethod.PUT)
    public void updateImportedPrograms(@PathVariable Integer departmentId,
            @Valid @RequestBody List<ImportedEntityDTO> importedPrograms) {
        departmentService.updateImportedPrograms(departmentId, importedPrograms);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public ActionOutcomeRepresentation inviteDepartment(@Valid @RequestBody DepartmentInvitationDTO departmentInvitationDTO) {
        ActionOutcomeDTO actionOutcomeDTO = departmentService.inviteDepartment(departmentInvitationDTO);
        return actionMapper.getActionOutcomeRepresentation(actionOutcomeDTO);
    }

}
