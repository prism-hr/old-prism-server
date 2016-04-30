package uk.co.alumeni.prism.mapping;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DESCRIPTION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_RELATED_INSTITUTIONS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobot;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobotMetadata;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Service
@Transactional
public class SystemMapper {

    @Value("${application.url}")
    private String applicationUrl;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ApplicationContext applicationContext;

    public ResourceRepresentationRobot getRobotsRepresentation() {
        System system = systemService.getSystem();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(system);
        return new ResourceRepresentationRobot(loader.loadLazy(SYSTEM_EXTERNAL_HOMEPAGE), applicationUrl).withSystem(
                new ResourceRepresentationRobotMetadata().withId(system.getId()).withAuthor(system.getUser().getFullName()).withName(system.getName())
                        .withSummmary(loader.loadLazy(SYSTEM_DESCRIPTION)).withThumbnailUrl(resourceMapper.getResourceThumbnailUrlRobot(system))
                        .withResourceUrl(resourceMapper.getResourceUrlRobot(system)))
                .withRelatedInstitutions(
                        resourceService.getResourceRobotRelatedRepresentations(system, INSTITUTION, loader.loadLazy(SYSTEM_OPPORTUNITIES_RELATED_INSTITUTIONS)));
    }

}
