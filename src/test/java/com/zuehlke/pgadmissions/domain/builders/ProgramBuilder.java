package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramBuilder {

    private Integer id;
    private String title = "Title.";
    private String description = "Description.";
    private Integer studyDuration = 12;
    private String funding;
    private Boolean active = true;
    private Boolean enabled = true;
    private Date lastEditedTimestamp;
    private RegisteredUser contactUser;
    private String code;
    private boolean atasRequired;
    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
    private List<ProgramClosingDate> programClosingDates = new ArrayList<ProgramClosingDate>();
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
    private QualificationInstitution institution;
    private ProgramFeed programFeed;
    private ProgramType programType = ProgramTypeBuilder.aProgramType(institution).build();
    private boolean locked;

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

    public ProgramBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public ProgramBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ProgramBuilder lastEditedTimestamp(Date lastEditedTimestamp) {
        this.lastEditedTimestamp = lastEditedTimestamp;
        return this;
    }

    public ProgramBuilder contactUser(RegisteredUser contactUser) {
        this.contactUser = contactUser;
        return this;
    }

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

    public ProgramBuilder closingDates(ProgramClosingDate... programClosingDates) {
        for (ProgramClosingDate programClosingDate : programClosingDates) {
            this.programClosingDates.add(programClosingDate);
        }
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

    public ProgramBuilder locked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public ProgramBuilder advert(Advert advert) {
        return id(advert.getId()).title(advert.getTitle()).description(advert.getDescription()).studyDuration(advert.getStudyDuration())
                .funding(advert.getFunding()).active(advert.isActive()).enabled(advert.isEnabled()).lastEditedTimestamp(advert.getLastEditedTimestamp())
                .contactUser(advert.getContactUser());
    }

    public Program build() {
        Program program = new Program();
        program.setId(id);
        program.setTitle(title);
        program.setDescription(description);
        program.setStudyDuration(studyDuration);
        program.setFunding(funding);
        program.setActive(active);
        program.setEnabled(enabled);
        program.setLastEditedTimestamp(lastEditedTimestamp);
        program.setContactUser(contactUser);
        program.setCode(code);
        program.getApprovers().addAll(approvers);
        program.getAdministrators().addAll(administrators);
        program.getInstances().addAll(instances);
        program.getViewers().addAll(viewers);
        program.getScoringDefinitions().putAll(scoringDefinitions);
        program.setAtasRequired(atasRequired);
        program.getClosingDates().addAll(programClosingDates);
        program.setInstitution(institution);
        program.setProgramFeed(programFeed);
        program.setProgramType(programType);
        program.setLocked(locked);
        return program;
    }

    public static ProgramBuilder aProgram(QualificationInstitution institution) {
        return new ProgramBuilder().code("AAA").title("Amazing program!").enabled(true).atasRequired(false).institution(institution);
    }
}
