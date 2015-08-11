package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationTargeting;

@Service
@Transactional
public class InstitutionMapper {

    @Value("${system.id}")
    private Integer systemId;
    
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
        return getInstitutionRepresentationSimple(institution, InstitutionRepresentationSimple.class);
    }

    public InstitutionRepresentationTargeting getInstitutionRepresentationTargeting(Institution institution, Double relevance, Double distance) {
        InstitutionRepresentationTargeting representation = getInstitutionRepresentationSimple(institution, InstitutionRepresentationTargeting.class);
        representation.setRelevance(BigDecimal.valueOf(relevance));
        representation.setDistance(BigDecimal.valueOf(distance));
        return representation;
    }
    
    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class);
        resourceMapper.appendResourceSummaryRepresentation(institution, representation);
        return representation;
    }
    
    public List<ResourceChildCreationRepresentation> getInstitutionChildCreationRepresentations(PrismScope targetScope) {
        return resourceMapper.getResourceChildCreationRepresentations(SYSTEM, systemId, targetScope);
    }

    private <T extends InstitutionRepresentationSimple> T getInstitutionRepresentationSimple(Institution institution, Class<T> returnType) {
        T representation = resourceMapper.getResourceRepresentationSimple(institution, returnType);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(institution.getAdvert()));
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
