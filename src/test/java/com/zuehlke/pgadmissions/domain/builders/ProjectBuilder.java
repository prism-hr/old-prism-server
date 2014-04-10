package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;

public class ProjectBuilder {

    private Integer id;
    private String title = "Title.";
    private String description = "Description.";
    private Integer studyDuration = 12;
    private String funding;
    private AdvertState state;
    private User contactUser;
    private Program program;

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

    public ProjectBuilder state(AdvertState state) {
        this.state = state;
        return this;
    }

    public ProjectBuilder contactUser(User contactUser) {
        this.contactUser = contactUser;
        return this;
    }

    public ProjectBuilder program(Program program) {
        this.program = program;
        return this;
    }

    public ProjectBuilder advert(Advert advert) {
        return id(advert.getId()).title(advert.getTitle()).description(advert.getDescription()).studyDuration(advert.getStudyDuration())
                .funding(advert.getFunding()).state(advert.getState()).contactUser(advert.getContactUser());
    }

    public Project build() {
        Project project = new Project();
        project.setId(id);
        project.setTitle(title);
        project.setDescription(description);
        project.setStudyDuration(studyDuration);
        project.setFunding(funding);
        project.setState(state);
        project.setContactUser(contactUser);
        project.setProgram(program);
        return project;
    }

}
