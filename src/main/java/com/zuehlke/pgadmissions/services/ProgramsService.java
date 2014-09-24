package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

@Service
@Transactional
public class ProgramsService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private QualificationInstitutionService qualificationInstitutionService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoleService roleService;

    public List<Program> getAllEnabledPrograms() {
        return programDAO.getAllEnabledPrograms();

    }

    public Advert getById(Integer advertId) {
        return programDAO.getById(advertId);
    }

    public void save(Advert advert) {
        programDAO.save(advert);
    }

    public Advert merge(Advert advert) {
        return programDAO.merge(advert);
    }

    public Program getProgramByCode(String code) {
        return programDAO.getProgramByCode(code);
    }

    public List<Program> getProgramsForWhichCanManageProjects(RegisteredUser user) {
        return programDAO.getProgramsForWhichUserCanManageProjects(user);
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

    public void removeAdvert(Integer advertId) {
        Advert advert = getById(advertId);
        if (advert != null) {
            advert.setEnabled(false);
            advert.setActive(false);
            programDAO.save(advert);
        }
    }

    public List<Project> listProjects(RegisteredUser user, Program program) {
        if (user.isInRole(user, Authority.SUPERADMINISTRATOR) || user.isAdminInProgramme(program)) {
            return programDAO.getProjectsForProgram(program);
        } else {
            return programDAO.getProjectsForProgramOfWhichAuthor(program, user);
        }
    }

    public String getDefaultClosingDate(Program program) {
        Date closingDate = programDAO.getNextClosingDate(program);
        String formattedDate = "null";
        if (closingDate != null) {
            formattedDate = new SimpleDateFormat("dd MMM yyyy").format(closingDate);
        }
        return formattedDate;
    }

    public void updateClosingDate(ProgramClosingDate closingDate) {
        Program program = closingDate.getProgram();
        program.setLastEditedTimestamp(new Date());
        programDAO.updateClosingDate(closingDate);
    }

    public void deleteClosingDateById(Integer programClosingDateId) {
        ProgramClosingDate programClosingDate = programDAO.getClosingDateById(programClosingDateId);
        programDAO.deleteClosingDate(programClosingDate);
    }

    public void addClosingDateToProgram(Program program, ProgramClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
        program.setLastEditedTimestamp(new Date());
        programDAO.save(program);
    }

    protected String generateNextProgramCode(QualificationInstitution institution) {
        Program lastCustomProgram = programDAO.getLastCustomProgram(institution);
        Integer codeNumber;
        if (lastCustomProgram != null) {
            codeNumber = Integer.valueOf(lastCustomProgram.getCode().split("_")[1]);
            codeNumber++;
        } else {
            codeNumber = 0;
        }
        return String.format("%s_%05d", institution.getCode(), codeNumber);
    }

    protected Program createOrGetProgram(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);

        Program program = opportunityRequest.getSourceProgram();

        if (program != null) {
            program = (Program) merge(program);
            program.setContactUser(thisBean.getContactUserForProgram(program, opportunityRequest.getAuthor()));
        } else {
            program = new Program();
            program.setEnabled(true);
            program.setContactUser(opportunityRequest.getAuthor());
        }

        if (program.getProgramFeed() == null) {
            if (program.getInstitution() == null || !Objects.equal(program.getInstitution().getCode(), opportunityRequest.getInstitutionCode())) {
                QualificationInstitution institution = qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest.getInstitutionCode(),
                        opportunityRequest.getInstitutionCountry(), opportunityRequest.getOtherInstitution());
                program.setInstitution(institution);
                program.setCode(thisBean.generateNextProgramCode(institution));
            }
            program.setTitle(opportunityRequest.getProgramTitle());
            program.setAtasRequired(opportunityRequest.getAtasRequired());
            program.setProgramType(opportunityRequest.getProgramType());
        }

        program.setDescription(opportunityRequest.getProgramDescription());
        program.setStudyDuration(opportunityRequest.getStudyDuration());
        program.setFunding(opportunityRequest.getFunding());
        program.setActive(opportunityRequest.getAcceptingApplications());

        save(program);
        return program;
    }

    protected void grantAdminPermissionsForProgram(RegisteredUser user, Program program) {
        if (!HibernateUtils.containsEntity(user.getInstitutions(), program.getInstitution())) {
            user.getInstitutions().add(program.getInstitution());
        }
        Role adminRole = roleService.getRoleByAuthority(Authority.ADMINISTRATOR);
        Role approverRole = roleService.getRoleByAuthority(Authority.APPROVER);
        if (!HibernateUtils.containsEntity(user.getRoles(), adminRole)) {
            user.getRoles().add(adminRole);
        }
        if (!HibernateUtils.containsEntity(user.getRoles(), approverRole)) {
            user.getRoles().add(approverRole);
        }
        if (!HibernateUtils.containsEntity(user.getProgramsOfWhichAdministrator(), program)) {
            user.getProgramsOfWhichAdministrator().add(program);
        }
        if (!HibernateUtils.containsEntity(user.getProgramsOfWhichApprover(), program)) {
            user.getProgramsOfWhichApprover().add(program);
        }
    }

    public Program saveProgramOpportunity(OpportunityRequest opportunityRequest) {
        ProgramsService thisBean = applicationContext.getBean(ProgramsService.class);

        Program program = thisBean.createOrGetProgram(opportunityRequest);

        if (program.getProgramFeed() == null) {
            programInstanceService.createRemoveProgramInstances(program, opportunityRequest.getStudyOptions(), opportunityRequest.getAdvertisingDeadlineYear());
        }

        thisBean.grantAdminPermissionsForProgram(opportunityRequest.getAuthor(), program);

        return program;
    }

    public boolean canChangeInstitution(RegisteredUser user, OpportunityRequest opportunityRequest) {
        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            return true;
        }

        Program existingProgram = opportunityRequest.getSourceProgram();
        if (existingProgram != null && existingProgram.getInstitution().getCode().equals(opportunityRequest.getInstitutionCode())) {
            return true;
        }

        for (QualificationInstitution institution : user.getInstitutions()) {
            if (institution.getCode().equals(opportunityRequest.getInstitutionCode())) {
                return true;
            }
        }

        return false;

    }

    public Advert getValidProgramProjectAdvert(String programCode, Integer advertId) {
        Advert advert = null;   
        if (advertId != null) {
            advert = programDAO.getAcceptingApplicationsById(advertId);
        }

        if (advert == null && programCode != null) {
            advert = programDAO.getProgamAcceptingApplicationsByCode(programCode);
        }

        if (advert == null) {
            throw new CannotApplyException();
        }

        return advert;
    }

    public List<ProgramType> getProgramTypes() {
        return programDAO.getProgamTypes();
    }

    public ProgramType getProgramTypeById(ProgramTypeId programTypeId) {
        return programDAO.getProgramTypeById(programTypeId);
    }

    public void deleteInactiveAdverts() {
        programDAO.deleteInactiveAdverts();
    }

    protected RegisteredUser getContactUserForProgram(Program program, RegisteredUser candidateUser) {
        List<RegisteredUser> administrators = program.getAdministrators();
        if (!administrators.isEmpty()) {
            if (administrators.contains(candidateUser)) {
                return candidateUser;
            } else {
                return administrators.get(0);
            }
        }
        return program.getContactUser();
    }

}