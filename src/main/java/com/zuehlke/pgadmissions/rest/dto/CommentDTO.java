package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

public class CommentDTO {

    private String content;

    private List<Integer> documents;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Integer> documents) {
        this.documents = documents;
    }
}
