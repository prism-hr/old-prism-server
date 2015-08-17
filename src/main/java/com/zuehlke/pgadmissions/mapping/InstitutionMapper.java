package com.zuehlke.pgadmissions.mapping;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

@Service
@Transactional
public class InstitutionMapper {

    @Value("${system.id}")
    private Integer systemId;

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class);
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class);
        resourceMapper.appendResourceSummaryRepresentation(institution, representation);
        return representation;
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType);
        representation.setCurrency(institution.getCurrency());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());
        return representation;
    }

}
