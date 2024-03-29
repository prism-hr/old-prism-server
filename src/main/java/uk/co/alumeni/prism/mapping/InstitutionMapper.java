package uk.co.alumeni.prism.mapping;

import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocation;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentationClient;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitutionMapper {

    @Inject
    private InstitutionService institutionService;

    @Inject
    private UserService userService;

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution, List<PrismRole> overridingRoles, User currentUser) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class, overridingRoles, currentUser);
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution, List<PrismRole> overridingRoles, User currentUser) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class, overridingRoles,
                currentUser);
        resourceMapper.appendResourceParentRepresentationSummary(institution, representation);
        return representation;
    }

    public List<ResourceRepresentationLocation> getInstitutionRepresentations(String query, String[] googleIds) {
        return institutionService.getInstitutions(query, googleIds).stream()
                .map(rr -> resourceMapper.getResourceRepresentationLocation(rr, userService.getCurrentUser()))
                .collect(Collectors.toList());
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType, List<PrismRole> overridingRoles,
            User currentUser) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType, overridingRoles, currentUser);
        representation.setCurrency(institution.getCurrency());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());
        return representation;
    }

}
