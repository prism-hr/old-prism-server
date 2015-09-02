package com.zuehlke.pgadmissions.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.representation.resource.DepartmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.DepartmentRepresentationClient;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

@Service
@Transactional
public class DepartmentMapper {

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ResourceMapper resourceMapper;

    public DepartmentRepresentation getDepartmentRepresentation(Department department, List<PrismRole> overridingRoles) {
        return getDepartmentRepresentation(department, DepartmentRepresentation.class, overridingRoles);
    }

    public DepartmentRepresentationClient getDepartmentRepresentationClient(Department department, List<PrismRole> overridingRoles) {
        DepartmentRepresentationClient representation = getDepartmentRepresentation(department, DepartmentRepresentationClient.class, overridingRoles);
        resourceMapper.appendResourceSummaryRepresentation(department, representation);
        return representation;
    }

    private <T extends DepartmentRepresentation> T getDepartmentRepresentation(Department department, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceParentRepresentation(department, returnType, overridingRoles);
        List<ImportedEntityResponse> programRepresentations = department.getImportedPrograms().stream()
                .map(program -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(program)).collect(Collectors.toList());
        representation.setImportedPrograms(programRepresentations);
        return representation;
    }

}
