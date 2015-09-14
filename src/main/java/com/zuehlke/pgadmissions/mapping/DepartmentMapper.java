package com.zuehlke.pgadmissions.mapping;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.representation.resource.DepartmentRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentDivisionRepresentation;

@Service
@Transactional
public class DepartmentMapper {

    @Inject
    private ResourceMapper resourceMapper;

    public ResourceParentDivisionRepresentation getDepartmentRepresentation(Department department, List<PrismRole> overridingRoles) {
        return getDepartmentRepresentation(department, ResourceParentDivisionRepresentation.class, overridingRoles);
    }

    public DepartmentRepresentationClient getDepartmentRepresentationClient(Department department, List<PrismRole> overridingRoles) {
        DepartmentRepresentationClient representation = getDepartmentRepresentation(department, DepartmentRepresentationClient.class, overridingRoles);
        resourceMapper.appendResourceSummaryRepresentation(department, representation);
        return representation;
    }

    private <T extends ResourceParentDivisionRepresentation> T getDepartmentRepresentation(Department department, Class<T> returnType, List<PrismRole> overridingRoles) {
        return resourceMapper.getResourceParentRepresentation(department, returnType, overridingRoles);
    }

}
