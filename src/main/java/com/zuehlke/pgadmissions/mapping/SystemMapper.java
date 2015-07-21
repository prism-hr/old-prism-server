package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_RELATED_INSTITUTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobot;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(system);
        return new ResourceRepresentationRobot(loader.load(SYSTEM_EXTERNAL_HOMEPAGE), applicationUrl).withSystem(
                new ResourceRepresentationRobotMetadata().withId(system.getId()).withAuthor(system.getUser().getFullName()).withName(system.getName())
                        .withSummmary(loader.load(SYSTEM_DESCRIPTION)).withThumbnailUrl(resourceMapper.getResourceThumbnailUrlRobot(system))
                        .withResourceUrl(resourceMapper.getResourceUrlRobot(system)))
                .withRelatedInstitutions(
                        resourceService.getResourceRobotRelatedRepresentations(system, INSTITUTION, loader.load(SYSTEM_OPPORTUNITIES_RELATED_INSTITUTIONS)));
    }

}
