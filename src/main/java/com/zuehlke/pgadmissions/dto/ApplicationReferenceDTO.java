package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.user.User;

public class ApplicationReferenceDTO {

    private User user;

    private String jobTitle;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private ImportedEntitySimple addressDomicile;

    private String phone;

    private Comment comment;

    public final User getUser() {
        return user;
    }

    public final void setUser(User user) {
        this.user = user;
    }

    public final String getJobTitle() {
        return jobTitle;
    }

    public final void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public final String getAddressLine1() {
        return addressLine1;
    }

    public final void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public final String getAddressLine2() {
        return addressLine2;
    }

    public final void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public final String getAddressTown() {
        return addressTown;
    }

    public final void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public final String getAddressRegion() {
        return addressRegion;
    }

    public final void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public final String getAddressCode() {
        return addressCode;
    }

    public final void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public ImportedEntitySimple getAddressDomicile() {
        return addressDomicile;
    }

    public void setAddressDomicile(ImportedEntitySimple addressDomicile) {
        this.addressDomicile = addressDomicile;
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = phone;
    }

    public final Comment getComment() {
        return comment;
    }

    public final void setComment(Comment comment) {
        this.comment = comment;
    }

    public ApplicationReferenceDTO withUser(User user) {
        this.user = user;
        return this;
    }

    public ApplicationReferenceDTO withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public ApplicationReferenceDTO withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public ApplicationReferenceDTO withAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public ApplicationReferenceDTO withAddressTown(String addressTown) {
        this.addressTown = addressTown;
        return this;
    }

    public ApplicationReferenceDTO withAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
        return this;
    }

    public ApplicationReferenceDTO withAddressCode(String addressCode) {
        this.addressCode = addressCode;
        return this;
    }

    public ApplicationReferenceDTO withAddressDomicile(ImportedEntitySimple addressDomicile) {
        this.addressDomicile = addressDomicile;
        return this;
    }

    public ApplicationReferenceDTO withPhone(String phone) {
        this.phone = phone;
        return this;
    }

}
