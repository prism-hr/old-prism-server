package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.WorkflowResourceVersion;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Service
@Transactional
public class LocalizationService {

    public <T extends WorkflowResourceVersion> T getLocalizedVersion(Resource resource, User user, List<T> versions) {
        PrismLocale userLocale = user == null ? null : user.getLocale();
        PrismLocale resourceLocale = resource.getLocale();
        
        T userVersion = null;
        T resourceVersion = null;
        
        for (T version : versions) {
            PrismLocale versionLocale = version.getLocale();
            if (versionLocale == userLocale) {
                userVersion = version;
            } else if (versionLocale == resourceLocale) {
                resourceVersion = version;
            }
        }
        
        return userVersion == null ? resourceVersion : userVersion;
    }
    
}
