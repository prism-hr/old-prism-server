package com.zuehlke.pgadmissions.services;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.resource.Project;

@Service
@Transactional
public class ProjectService {

    @Inject
    private EntityService entityService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

}
