package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.domain.user.User;

@Entity
@Table(name = "APPLICATION_REFEREE", uniqueConstraints = {@UniqueConstraint(columnNames = {"application_id", "user_id"})})
public class ApplicationReferee extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, updatable = false, insertable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "skype")
    private String skype;

    @Column(name = "job_employer")
    private String jobEmployer;

    @Column(name = "job_title")
    private String jobTitle;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public DateTime getLastEditedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastEditedTimestamp(DateTime lastEditedTimestamp) {
        this.lastUpdatedTimestamp = lastEditedTimestamp;
    }

    public ApplicationReferee withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationReferee withUser(User user) {
        this.user = user;
        return this;
    }

    public ApplicationReferee withApplication(Application application) {
        this.application = application;
        return this;
    }

    public String getAddressDisplay() {
        return address == null ? null : address.getLocationString();
    }

}
