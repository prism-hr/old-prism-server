package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramBuilder {

    private Integer id;
    private String code;
    private String title;
    private boolean enabled;
    private boolean atasRequired;
    private boolean locked;

    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
    private List<ProgramClosingDate> programClosingDates = new ArrayList<ProgramClosingDate>();
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
    private List<Project> projects = Lists.newArrayList();
    private Advert advert;
    private QualificationInstitution institution;
    private ProgramFeed programFeed;
    private ProgramType programType;

    public ProgramBuilder atasRequired(boolean flag) {
        atasRequired = flag;
        return this;
    }

    public ProgramBuilder viewers(RegisteredUser... users) {
        for (RegisteredUser approver : users) {
            this.viewers.add(approver);
        }
        return this;
    }

    public ProgramBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public ProgramBuilder locked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public ProgramBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProgramBuilder instances(ProgramInstance... instances) {
        for (ProgramInstance instance : instances) {
            this.instances.add(instance);
        }
        return this;
    }

    public ProgramBuilder approver(RegisteredUser... approvers) {
        for (RegisteredUser approver : approvers) {
            this.approvers.add(approver);
        }
        return this;
    }

    public ProgramBuilder administrators(RegisteredUser... administrators) {
        for (RegisteredUser administrator : administrators) {
            this.administrators.add(administrator);
        }
        return this;
    }

    public ProgramBuilder scoringDefinitions(Map<ScoringStage, ScoringDefinition> scoringDefinitions) {
        this.scoringDefinitions.putAll(scoringDefinitions);
        return this;
    }

    public ProgramBuilder code(String code) {
        this.code = code;
        return this;
    }

    public ProgramBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProgramBuilder closingDates(ProgramClosingDate... programClosingDates) {
        this.programClosingDates.addAll(Arrays.asList(programClosingDates));
        return this;
    }

    public ProgramBuilder projects(Project... projects) {
        this.projects.addAll(Arrays.asList(projects));
        return this;
    }

    public ProgramBuilder advert(Advert advert) {
        this.advert = advert;
        return this;
    }
    
    public ProgramBuilder institution(QualificationInstitution institution) {
        this.institution = institution;
        return this;
    }

    public ProgramBuilder programFeed(ProgramFeed programFeed) {
        this.programFeed = programFeed;
        return this;
    }

    public ProgramBuilder programType(ProgramType programType) {
        this.programType = programType;
        return this;
    }

    public Program build() {
        Program program = new Program();
        program.setId(id);
        program.setCode(code);
        program.setTitle(title);
        program.setEnabled(enabled);
        program.setLocked(locked);
        program.getApprovers().addAll(approvers);
        program.getAdministrators().addAll(administrators);
        program.getInstances().addAll(instances);
        program.getViewers().addAll(viewers);
        program.getScoringDefinitions().putAll(scoringDefinitions);
        program.setAtasRequired(atasRequired);
        program.getClosingDates().addAll(programClosingDates);
        program.getProjects().addAll(projects);
        program.setAdvert(advert);
        program.setInstitution(institution);
        program.setProgramFeed(programFeed);
        program.setProgramType(programType);
        return program;
    }
    
    public static ProgramBuilder aProgram(QualificationInstitution institution) {
        return new ProgramBuilder().code("AAA").title("Amazing program!").enabled(true).atasRequired(false).institution(institution);
    }
}
