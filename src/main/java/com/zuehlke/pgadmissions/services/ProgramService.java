package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Service
@Transactional
public class ProgramService {

    @Inject
    private ProgramDAO programDAO;

    @Inject
    private EntityService entityService;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        entityService.save(program);
    }

    public List<ResourceRepresentationSimple> getApprovedPrograms(Integer institutionId) {
        return programDAO.getApprovedPrograms(institutionId);
    }

    public List<ResourceRepresentationSimple> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return programDAO.getSimilarPrograms(institutionId, searchTerm);
    }

    public List<Integer> getProjects(Integer program) {
        return programDAO.getProjects(program);
    }

    public List<Integer> getApplications(Integer program) {
        return programDAO.getApplications(program);
    }

}
