package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ProjectBuilder {
 
    private Integer id;
    private String title = "Title.";
    private String description = "Description.";
    private Integer studyDuration = 12;
    private String funding;
    private Boolean active = true;
    private Boolean enabled = true;
    private Date lastEditedTimestamp;
    private RegisteredUser contactUser;
    private Program program;
    private Date closingDate;
    private RegisteredUser administrator;
    private RegisteredUser primarySupervisor;
    private RegisteredUser secondarySupervisor;
    
    public ProjectBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProjectBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProjectBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder studyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
        return this;
    }

    public ProjectBuilder funding(String funding) {
        this.funding = funding;
        return this;
    }

    public ProjectBuilder active(boolean active) {
        this.active = active;
        return this;
    }
    
    public ProjectBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public ProjectBuilder lastEditedTimestamp(Date lastEditedTimestamp) {
        this.lastEditedTimestamp = lastEditedTimestamp;
        return this;
    }
    
    public ProjectBuilder contactUser(RegisteredUser contactUser) {
        this.contactUser = contactUser;
        return this;
    }
    
    public ProjectBuilder program(Program program) {
        this.program = program;
        return this;
    }
    
    public ProjectBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate;
        return this;
    }
    
    public ProjectBuilder administrator(RegisteredUser administrator) {
        this.administrator = administrator;
        return this;
    }
   
    public ProjectBuilder primarySupervisor(RegisteredUser primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
        return this;
    }
    
    public ProjectBuilder secondarySupervisor(RegisteredUser secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
        return this;
    }
    
    public ProjectBuilder advert(Advert advert) {
        return id(advert.getId()).title(advert.getTitle()).description(advert.getDescription()).studyDuration(advert.getStudyDuration())
                .funding(advert.getFunding()).active(advert.isActive()).enabled(advert.isEnabled()).lastEditedTimestamp(advert.getLastEditedTimestamp())
                .contactUser(advert.getContactUser());
    }
    
    public Project build() {
        Project project = new Project();
        project.setId(id);
        project.setTitle(title);
        project.setDescription(description);
        project.setStudyDuration(studyDuration);
        project.setFunding(funding);
        project.setActive(active);
        project.setEnabled(enabled);
        project.setLastEditedTimestamp(lastEditedTimestamp);
        project.setContactUser(contactUser);
        project.setProgram(program);
        project.setClosingDate(closingDate);
        project.setAdministrator(administrator);
        project.setPrimarySupervisor(primarySupervisor);
        project.setSecondarySupervisor(secondarySupervisor);
        return project;
    }

}
