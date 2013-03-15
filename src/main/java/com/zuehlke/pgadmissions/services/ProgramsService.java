package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Program;

@Service
@Transactional
public class ProgramsService {

    private final ProgramDAO programDAO;

    ProgramsService() {
        this(null);
    }

    @Autowired
    public ProgramsService(ProgramDAO programDAO) {
        this.programDAO = programDAO;
    }

    public List<Program> getAllPrograms() {
        return programDAO.getAllPrograms();
    }

    public Program getProgramById(Integer programId) {
        return programDAO.getProgramById(programId);
    }

    public void save(Program program) {
        programDAO.save(program);
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }
}
