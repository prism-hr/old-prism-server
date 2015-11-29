package uk.co.alumeni.prism.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocation;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentationClient;
import uk.co.alumeni.prism.services.InstitutionService;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private InstitutionService institutionService;

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

    public List<ResourceRepresentationLocation> getInstitutionRepresentations(String query, String[] googleIds) {
        return institutionService.getInstitutions(query, googleIds).stream().map(resourceMapper::getResourceRepresentationCreation).collect(Collectors.toList());
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType, overridingRoles);
        representation.setCurrency(institution.getCurrency());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());
        return representation;
    }

}
