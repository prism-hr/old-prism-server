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
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;

@Service
@Transactional
public class ProgramService {

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
        programDAO.merge(advert);
        return advert;
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
        // TODO implement
        return null;
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
        programDAO.updateClosingDate(closingDate);
    }

    public void deleteClosingDateById(Integer programClosingDateId) {
        ProgramClosingDate programClosingDate = programDAO.getClosingDateById(programClosingDateId);
        programDAO.deleteClosingDate(programClosingDate);
    }

    public void addClosingDateToProgram(Program program, ProgramClosingDate programClosingDate) {
        program.getClosingDates().add(programClosingDate);
        programDAO.save(program);
    }

    protected String generateNextProgramCode(Institution institution) {
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
        ProgramService thisBean = applicationContext.getBean(ProgramService.class);

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
                Institution institution = qualificationInstitutionService.getOrCreate(opportunityRequest.getInstitutionCode(),
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
        // TODO try to reuse any method from RoleService
        throw new UnsupportedOperationException();
//        if (!HibernateUtils.containsEntity(user.getInstitutions(), program.getInstitution())) {
//            user.getInstitutions().add(program.getInstitution());
//        }
//        Role adminRole = roleService.getById(Authority.ADMINISTRATOR);
//        Role approverRole = roleService.getById(Authority.APPROVER);
//        if (!HibernateUtils.containsEntity(user.getRoles(), adminRole)) {
//            user.getRoles().add(adminRole);
//        }
//        if (!HibernateUtils.containsEntity(user.getRoles(), approverRole)) {
//            user.getRoles().add(approverRole);
//        }
//        if (!HibernateUtils.containsEntity(user.getProgramsOfWhichAdministrator(), program)) {
//            user.getProgramsOfWhichAdministrator().add(program);
//        }
//        if (!HibernateUtils.containsEntity(user.getProgramsOfWhichApprover(), program)) {
//            user.getProgramsOfWhichApprover().add(program);
//        }
    }

    public Program saveProgramOpportunity(OpportunityRequest opportunityRequest) {
        ProgramService thisBean = applicationContext.getBean(ProgramService.class);

        Program program = thisBean.createOrGetProgram(opportunityRequest);

        if (program.getProgramFeed() == null) {
            programInstanceService.createRemoveProgramInstances(program, opportunityRequest.getStudyOptions(), opportunityRequest.getAdvertisingDeadlineYear());
        }

        thisBean.grantAdminPermissionsForProgram(opportunityRequest.getAuthor(), program);

        return program;
    }

    public boolean canChangeInstitution(RegisteredUser user, OpportunityRequest opportunityRequest) {
        if (roleService.hasRole(user, Authority.SUPERADMINISTRATOR)) {
            return true;
        }

        Program existingProgram = opportunityRequest.getSourceProgram();
        if (existingProgram != null && existingProgram.getInstitution().getCode().equals(opportunityRequest.getInstitutionCode())) {
            return true;
        }

        // TODO reimplement
//        for (Institution institution : user.getInstitutions()) {
//            if (institution.getCode().equals(opportunityRequest.getInstitutionCode())) {
//                return true;
//            }
//        }

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
    
    public Date getDefaultStartDate(Program program, StudyOption studyOption) {
        return programDAO.getDefaultStartDate(program, studyOption);
    }
    
    public List<ProgramInstance> getActiveProgramInstances(Program program) {
        return programDAO.getActiveProgramInstances(program);
    }
    
    public List<ProgramInstance> getActiveProgramInstancesForStudyOption(Program program, StudyOption studyOption) {
        return programDAO.getActiveProgramInstancesForStudyOption(program, studyOption);
    }
    
    public List<StudyOption> getAvailableStudyOptions(Program program) {
        return programDAO.getAvailableStudyOptions(program);
    }
    
    public List<StudyOption> getAvailableStudyOptions() {
        return programDAO.getAvailableStudyOptions();
    }

    public Date getNextClosingDate(Program program) {
        return programDAO.getNextClosingDate(program);
    }
    
    private RegisteredUser getContactUserForProgram(Program program, RegisteredUser candidateUser) {
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
