package uk.co.alumeni.prism.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.domain.resource.Program;

import javax.inject.Inject;

@Service
@Transactional
public class ProgramService {

    @Inject
    private EntityService entityService;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

}
