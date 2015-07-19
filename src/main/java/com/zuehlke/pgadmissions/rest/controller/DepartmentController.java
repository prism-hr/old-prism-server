package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/departments")
@PreAuthorize("permitAll")
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ResourceMapper resourceMapper;

    @RequestMapping(method = RequestMethod.GET, params = "type=simple")
    public List<ResourceRepresentationSimple> getDepartments(@RequestParam(required = false) String query) {
        return departmentService.getDepartments(query).stream()
                .map(resourceMapper::getResourceRepresentationSimple)
                .collect(Collectors.toList());
    }

}
