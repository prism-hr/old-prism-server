package uk.co.alumeni.prism.rest.representation.profile;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileAwardRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private String name;

    private String description;

    private LocalDate awardDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public ProfileAwardRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProfileAwardRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public ProfileAwardRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProfileAwardRepresentation withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

}
