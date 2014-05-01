package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramImport;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramBuilder {

    private Integer id;
    private String title = "Title.";
    private String description = "Description.";
    private Integer studyDuration = 12;
    private String funding;
    private AdvertState state;
    private User contactUser;
    private String code;
    private boolean atasRequired;
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
    private List<AdvertClosingDate> programClosingDates = new ArrayList<AdvertClosingDate>();
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
    private List<Project> projects = Lists.newArrayList();
    private Institution institution;
    private ProgramImport programFeed;
    private ProgramType programType = ProgramTypeBuilder.aProgramType().build();

    public ProgramBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProgramBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProgramBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProgramBuilder studyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
        return this;
    }

    public ProgramBuilder funding(String funding) {
        this.funding = funding;
        return this;
    }

    public ProgramBuilder state(AdvertState state) {
        this.state = state;
        return this;
    }

    public ProgramBuilder contactUser(User contactUser) {
        this.contactUser = contactUser;
        return this;
    }

    public ProgramBuilder atasRequired(boolean flag) {
        atasRequired = flag;
        return this;
    }

    public ProgramBuilder instances(ProgramInstance... instances) {
        for (ProgramInstance instance : instances) {
            this.instances.add(instance);
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

    public ProgramBuilder closingDates(AdvertClosingDate... programClosingDates) {
        this.programClosingDates.addAll(Arrays.asList(programClosingDates));
        return this;
    }

    public ProgramBuilder projects(Project... projects) {
        this.projects.addAll(Arrays.asList(projects));
        return this;
    }

    public ProgramBuilder institution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ProgramBuilder programFeed(ProgramImport programFeed) {
        this.programFeed = programFeed;
        return this;
    }

    public ProgramBuilder programType(ProgramType programType) {
        this.programType = programType;
        return this;
    }

    public ProgramBuilder advert(Advert advert) {
        return id(advert.getId()).title(advert.getTitle()).description(advert.getDescription()).studyDuration(advert.getStudyDuration())
                .funding(advert.getFunding()).state(advert.getState()).contactUser(advert.getContactUser());
    }

    public Program build() {
        Program program = new Program();
        program.setId(id);
        program.setTitle(title);
        program.setDescription(description);
        program.setStudyDuration(studyDuration);
        program.setFunding(funding);
        program.setContactUser(contactUser);
        program.setCode(code);
        program.getInstances().addAll(instances);
        program.getScoringDefinitions().putAll(scoringDefinitions);
        program.setAtasRequired(atasRequired);
        program.getClosingDates().addAll(programClosingDates);
        program.getProjects().addAll(projects);
        program.setInstitution(institution);
        program.setProgramImport(programFeed);
        program.setProgramType(programType);
        program.setState(state);
        return program;
    }

    public static ProgramBuilder aProgram(Institution institution) {
        return new ProgramBuilder().code("AAA").title("Amazing program!").state(AdvertState.PROGRAM_APPROVED).atasRequired(false).institution(institution);
    }
}
