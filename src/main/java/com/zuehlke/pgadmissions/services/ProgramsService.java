package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ProjectAdvertDTO;

@Service
@Transactional
public class ProgramsService {

    private final ProgramDAO programDAO;
    private final AdvertDAO advertDAO;

    ProgramsService() {
        this(null, null);
    }

    @Autowired
    public ProgramsService(ProgramDAO programDAO, AdvertDAO advertDAO) {
        this.programDAO = programDAO;
        this.advertDAO = advertDAO;
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

    public void applyScoringDefinition(String programCode, ScoringStage scoringStage, String scoringContent) {
        Program program = programDAO.getProgramByCode(programCode);
        ScoringDefinition scoringDefinition = new ScoringDefinition();
        scoringDefinition.setContent(scoringContent);
        scoringDefinition.setStage(scoringStage);
        program.getScoringDefinitions().put(scoringStage, scoringDefinition);
    }

    public void removeScoringDefinition(String programCode, ScoringStage scoringStage) {
        Program program = programDAO.getProgramByCode(programCode);
        program.getScoringDefinitions().put(scoringStage, null);
    }

    public Advert getProgramAdvert(Program program) {
        return advertDAO.getProgramAdvert(program);
    }

    public void merge(Advert programAdvert) {
        advertDAO.merge(programAdvert);
    }

    public void removeAdvert(int advertId) {
        Advert advert = advertDAO.getAdvertById(advertId);
        advertDAO.delete(advert);
    }

    public Advert getAdvert(int advertId) {
        return advertDAO.getAdvertById(advertId);
    }

    public void addProjectAdvert(ProjectAdvertDTO projectAdvertDTO) {
        Advert advert = new Advert();
        advert.setProgram(projectAdvertDTO.getProgram());
        advert.setTitle(projectAdvertDTO.getTitle());
        advert.setDescription(projectAdvertDTO.getDescription());
        advert.setStudyDuration(projectAdvertDTO.getStudyDuration());
        advert.setFunding(projectAdvertDTO.getFunding());
        advert.setActive(projectAdvertDTO.getActive());
        advert.setIsProgramAdvert(false);

        advertDAO.save(advert);
    }

    public List<Advert> listProjectAdverts() {
        return advertDAO.listProjectAdverts();
    }

}
