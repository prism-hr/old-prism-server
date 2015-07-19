package com.zuehlke.pgadmissions.mapping;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationSimple;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class);
    }

    public InstitutionRepresentationSimple getInstitutionRepresentationSimple(Institution institution) {
        InstitutionRepresentationSimple representation = resourceMapper.getResourceRepresentation(institution, InstitutionRepresentationSimple.class);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(institution.getAdvert()));
        return representation;
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class);
        resourceMapper.appendResourceSummaryRepresentation(institution, representation);
        return representation;
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType);

        representation.setCurrency(institution.getCurrency());
        representation.setMinimumWage(institution.getMinimumWage());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());

        return representation;
    }

}
