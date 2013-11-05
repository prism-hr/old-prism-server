package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "APPLICATION_FORM_USER_ROLE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm applicationForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_user_id")
    private RegisteredUser user;

    @ManyToOne
    @JoinColumn(name = "application_role_id")
    private Role role;

    @Column(name = "is_interested_in_applicant")
    private Boolean interestedInApplicant = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_user_role_id", nullable = false)
    private List<ApplicationFormActionRequired> actions = new ArrayList<ApplicationFormActionRequired>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean isInterestedInApplicant() {
        return interestedInApplicant;
    }

    public void setInterestedInApplicant(Boolean isInterestedInApplicant) {
        this.interestedInApplicant = isInterestedInApplicant;
    }

    public List<ApplicationFormActionRequired> getActions() {
        return actions;
    }

}
