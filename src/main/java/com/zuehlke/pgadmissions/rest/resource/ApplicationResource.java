package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.domain.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.domain.ResourceRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailsDTO;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = { "api/applications" })
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/{applicationId}/programDetails", method = RequestMethod.PUT)
    public void saveProgramDetails(@PathVariable Integer applicationId, @RequestBody ApplicationProgramDetailsDTO programDetailsDTO) {
        applicationService.saveProgramDetails(applicationId, programDetailsDTO);

    }

}
