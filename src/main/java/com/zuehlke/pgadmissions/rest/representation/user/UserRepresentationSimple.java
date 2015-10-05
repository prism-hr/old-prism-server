package com.zuehlke.pgadmissions.rest.representation.user;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

public class UserRepresentationSimple extends UserRepresentation {

    private Integer id;

    private String firstName2;

    private String firstName3;

    private String fullName;

    private String accountProfileUrl;

    private String accountImageUrl;

    private DocumentRepresentation portraitImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public void setAccountProfileUrl(String linkedinProfileUrl) {
        this.accountProfileUrl = linkedinProfileUrl;
    }

    public String getAccountImageUrl() {
        return accountImageUrl;
    }

    public void setAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
    }

    public DocumentRepresentation getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(DocumentRepresentation portraitImage) {
        this.portraitImage = portraitImage;
    }

    public UserRepresentationSimple withId(Integer id) {
        setId(id);
        return this;
    }

    public UserRepresentationSimple withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public UserRepresentationSimple withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public UserRepresentationSimple withEmail(String email) {
        setEmail(email);
        return this;
    }

    public UserRepresentationSimple withAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
        return this;
    }

    public UserRepresentationSimple withAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
        return this;
    }

    public UserRepresentationSimple withPortraitImage(DocumentRepresentation portraitImage) {
        this.portraitImage = portraitImage;
        return this;
    }
    
}
