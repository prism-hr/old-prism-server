package com.zuehlke.pgadmissions.mapping;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution, List<PrismRole> overridingRoles) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class, overridingRoles);
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution, List<PrismRole> overridingRoles) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class, overridingRoles);
        resourceMapper.appendResourceSummaryRepresentation(institution, representation);
        return representation;
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType, overridingRoles);
        representation.setCurrency(institution.getCurrency());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());
        return representation;
    }

}
