package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class EntityCreationService {

    @Autowired
    private ApplicationFormService applicationService;

    public PrismScope create(User user, PrismScope scope, String newScopeName) {
        if ("application".equals(newScopeName)) {
            return applicationService.getOrCreateApplication(user, scope.getId());
        }
        throw new IllegalArgumentException("Unknown scope name: " + newScopeName);
    }

}
