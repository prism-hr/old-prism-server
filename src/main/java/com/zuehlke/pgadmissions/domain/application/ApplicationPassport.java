package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.LocaleUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_PASSPORT")
public class ApplicationPassport extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "passport")
    private ApplicationPersonalDetail applicationPersonalDetail;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "issue_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate expiryDate;
    
    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    @Override
    public DateTime getLastEditedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastEditedTimestamp(DateTime lastEditedTimestamp) {
        this.lastUpdatedTimestamp = lastEditedTimestamp;
    }

    public ApplicationPassport withNumber(String number) {
        this.number = number;
        return this;
    }

    public ApplicationPassport withName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationPassport withIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public ApplicationPassport withExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public String getIssueDateDisplay(String dateFormat) {
        return issueDate == null ? null : issueDate.toString(dateFormat,
                LocaleUtils.toLocale(applicationPersonalDetail.getApplication().getLocale().toString()));
    }

    public String getExipryDateDisplay(String dateFormat) {
        return expiryDate == null ? null : expiryDate.toString(dateFormat,
                LocaleUtils.toLocale(applicationPersonalDetail.getApplication().getLocale().toString()));
    }

}
