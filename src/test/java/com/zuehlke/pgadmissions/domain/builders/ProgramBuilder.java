package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramBuilder {

    private Integer id;
    private String code;
    private String title;
    private boolean enabled;
    private boolean atasRequired;

    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> interviewers = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> supervisors = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
    private List<ProgramClosingDate> programClosingDates = new ArrayList<ProgramClosingDate>();
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
    private Advert advert;

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

    public ProgramBuilder reviewers(RegisteredUser... reviewers) {
        for (RegisteredUser reviewer : reviewers) {
            this.reviewers.add(reviewer);
        }
        return this;
    }

    public ProgramBuilder interviewers(RegisteredUser... interviewers) {
        for (RegisteredUser interviewer : interviewers) {
            this.interviewers.add(interviewer);
        }
        return this;
    }

    public ProgramBuilder supervisors(RegisteredUser... supervisors) {
        for (RegisteredUser supervisor : supervisors) {
            this.supervisors.add(supervisor);
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
        for (ProgramClosingDate programClosingDate : programClosingDates) {
            this.programClosingDates.add(programClosingDate);
        }
        return this;
    }

    public ProgramBuilder adverts(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Program build() {
        Program program = new Program();
        program.setId(id);
        program.setCode(code);
        program.setTitle(title);
        program.setEnabled(enabled);
        program.getApprovers().addAll(approvers);
        program.getAdministrators().addAll(administrators);
        program.getProgramReviewers().addAll(reviewers);
        program.getInterviewers().addAll(interviewers);
        program.getSupervisors().addAll(supervisors);
        program.getInstances().addAll(instances);
        program.getViewers().addAll(viewers);
        program.getScoringDefinitions().putAll(scoringDefinitions);
        program.setAtasRequired(atasRequired);
        program.getClosingDates().addAll(programClosingDates);
        program.setAdvert(advert);
        return program;
    }
}
