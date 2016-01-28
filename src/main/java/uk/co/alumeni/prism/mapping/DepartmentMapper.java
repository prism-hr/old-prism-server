package uk.co.alumeni.prism.mapping;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.resource.DepartmentRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentation;

@Service
@Transactional
public class DepartmentMapper {

    @Inject
    private ResourceMapper resourceMapper;

    public ResourceParentRepresentation getDepartmentRepresentation(Department department, List<PrismRole> overridingRoles, User currentUser) {
        return getDepartmentRepresentation(department, ResourceParentRepresentation.class, overridingRoles, currentUser);
    }

    public DepartmentRepresentationClient getDepartmentRepresentationClient(Department department, List<PrismRole> overridingRoles, User currentUser) {
        DepartmentRepresentationClient representation = getDepartmentRepresentation(department, DepartmentRepresentationClient.class, overridingRoles,
                currentUser);
        resourceMapper.appendResourceSummaryRepresentation(department, representation);
        return representation;
    }

    private <T extends ResourceParentRepresentation> T getDepartmentRepresentation(Department department, Class<T> returnType, List<PrismRole> overridingRoles,
            User currentUser) {
        return resourceMapper.getResourceParentRepresentation(department, returnType, overridingRoles, currentUser);
    }

}
