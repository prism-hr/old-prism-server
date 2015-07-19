package com.zuehlke.pgadmissions.mapping;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationSimple;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private AddressMapper addressMapper;

    @Inject DocumentMapper documentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class);
    }

    public InstitutionRepresentationSimple getInstitutionRepresentationSimple(Institution institution) {
        return new InstitutionRepresentationSimple().withId(institution.getId()).withTitle(institution.getTitle())
                .withAddress(addressMapper.transform(institution.getAdvert().getAddress(), AddressAdvertRepresentation.class))
                .withCode(institution.getCode()).withLogoImage(documentMapper.getDocumentRepresentation(institution.getLogoImage()))
                .withScope(institution.getResourceScope());
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
