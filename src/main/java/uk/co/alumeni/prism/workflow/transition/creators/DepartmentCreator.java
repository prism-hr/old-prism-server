package uk.co.alumeni.prism.workflow.transition.creators;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

@Component
public class DepartmentCreator implements ResourceCreator<ResourceParentDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, ResourceParentDTO newResource) {
        Institution institution = resourceCreatorUtils.getParentResource(newResource);

        Advert advert = advertService.createResourceAdvert(newResource, institution, user);
        Department department = new Department().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(institution).withAdvert(advert)
                .withName(advert.getName());

        List<PrismOpportunityCategory> opportunityCategories = newResource.getOpportunityCategories();
        newResource.setOpportunityCategories(isEmpty(opportunityCategories)
                ? asList(institution.getOpportunityCategories().split("\\|")).stream().map(PrismOpportunityCategory::valueOf).collect(toList()) : opportunityCategories);

        resourceService.setResourceAttributes(department, newResource);
        return department;
    }

}
