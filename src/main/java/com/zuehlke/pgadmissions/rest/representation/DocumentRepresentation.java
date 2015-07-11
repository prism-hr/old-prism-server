package com.zuehlke.pgadmissions.rest.representation;

/**
 * I am making an effort to keep the naming and package structuring consistent in different layers of the application, to keep the codebase as manageable as possible, e.g.
 * Document (Entity) => DocumentRepresentation (Representation), DocumentDTO (DTO), or
 * File (Entity) => FileRepresentation (Representation), FileDTO (DTO) 
 * We try our best not to mix it up
 * @author alastair
 *
 */
public class DocumentRepresentation {

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

    public DocumentRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public DocumentRepresentation withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

}
