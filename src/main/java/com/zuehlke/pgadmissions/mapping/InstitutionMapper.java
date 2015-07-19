package com.zuehlke.pgadmissions.mapping;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution) throws Exception {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class);
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution) throws Exception {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class);
        representation.setResourceSummary(resourceMapper.getResourceSummaryRepresentation(institution));
        return representation;
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType) throws Exception {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType);

        representation.setCurrency(institution.getCurrency());
        representation.setMinimumWage(institution.getMinimumWage());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());

        return representation;
    }

}
