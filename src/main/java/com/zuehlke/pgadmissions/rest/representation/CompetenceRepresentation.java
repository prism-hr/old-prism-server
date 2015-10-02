package com.zuehlke.pgadmissions.rest.representation;

public class CompetenceRepresentation {

    private Integer id;

    private String name;

    private String description;

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

    public CompetenceRepresentation withId(final Integer id) {
        this.id = id;
        return this;
    }

    public CompetenceRepresentation withName(final String name) {
        this.name = name;
        return this;
    }

    public CompetenceRepresentation withDescription(final String description) {
        this.description = description;
        return this;
    }

}
