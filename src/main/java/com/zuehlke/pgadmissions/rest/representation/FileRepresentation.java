package com.zuehlke.pgadmissions.rest.representation;

public class FileRepresentation {

    private Integer id;

    private String fileName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public FileRepresentation withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

}
