package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertSubjectAreaRepresentation {

    private Integer id;

    private String name;

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

    public AdvertSubjectAreaRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertSubjectAreaRepresentation withName(String name) {
        this.name = name;
        return this;
    }

}
