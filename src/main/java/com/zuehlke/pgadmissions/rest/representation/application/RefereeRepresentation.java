package com.zuehlke.pgadmissions.rest.representation.application;

import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class RefereeRepresentation {

    private Integer id;

    private UserRepresentation user;

    private String phoneNumber;

    private String skype;

    private String jobEmployer;

    private String jobTitle;

    private AddressRepresentation address;

    private Boolean includeInExport;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getJobEmployer() {
        return jobEmployer;
    }

    public void setJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }

    public Boolean getIncludeInExport() {
        return includeInExport;
    }

    public void setIncludeInExport(Boolean includeInExport) {
        this.includeInExport = includeInExport;
    }
}
