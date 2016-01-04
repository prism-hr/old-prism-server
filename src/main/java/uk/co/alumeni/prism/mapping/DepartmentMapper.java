package uk.co.alumeni.prism.mapping;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.rest.representation.resource.DepartmentRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentation;

@Service
@Transactional
public class DepartmentMapper {

    @Inject
    private ResourceMapper resourceMapper;

    public ResourceParentRepresentation getDepartmentRepresentation(Department department, List<PrismRole> overridingRoles) {
        return getDepartmentRepresentation(department, ResourceParentRepresentation.class, overridingRoles);
    }

    public DepartmentRepresentationClient getDepartmentRepresentationClient(Department department, List<PrismRole> overridingRoles) {
        DepartmentRepresentationClient representation = getDepartmentRepresentation(department, DepartmentRepresentationClient.class, overridingRoles);
        resourceMapper.appendResourceSummaryRepresentation(department, representation);
        return representation;
    }

    private <T extends ResourceParentRepresentation> T getDepartmentRepresentation(Department department, Class<T> returnType, List<PrismRole> overridingRoles) {
        return resourceMapper.getResourceParentRepresentation(department, returnType, overridingRoles);
    }

}
