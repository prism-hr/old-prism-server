package com.zuehlke.pgadmissions.rest.representation.resource;

public class SimpleResourceRepresentation {

    private Integer id;

    private String title;

    public SimpleResourceRepresentation() {
        return;
    }

    public SimpleResourceRepresentation(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
