package uk.co.alumeni.prism.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class DocumentDTO {

    @NotNull
    private Integer id;

    @NotEmpty
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

    public DocumentDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public DocumentDTO withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

}
