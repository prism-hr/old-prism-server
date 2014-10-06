package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.WorkflowResourceLocalized;
import com.zuehlke.pgadmissions.domain.WorkflowResourceVersion;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Service
@Transactional
public class LocalizationService {

    @Autowired
    private ConfigurationService configurationService;

    public <T extends WorkflowResourceLocalized<V>, U extends WorkflowDefinition, V extends WorkflowResourceVersion> V getVersion(
            Class<T> localizedEntityClass, Resource resource, U parameter, PrismLocale userLocale, PrismLocale resourceLocale) {
        WorkflowResourceLocalized<V> localized = configurationService.getConfiguration(localizedEntityClass, resource, parameter);
        V version = localized.getVersion(userLocale);

        if (version == null) {
            version = localized.getVersion(resourceLocale);
        }

        if (version == null) {
            Resource parentResource = resource.getParentResource();
            if (resource == parentResource) {
                return null;
            }
            return getVersion(localizedEntityClass, parentResource, parameter, userLocale, resourceLocale);
        }

        return (V) version;
    }

}
