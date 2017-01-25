package uk.co.alumeni.prism.rest.representation;

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
