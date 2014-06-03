package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "APPLICATION_SUPERVISOR")
public class SuggestedSupervisor {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_program_detail_id", nullable = false, unique = true)
    private ProgramDetails programDetails;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "aware_of_application", nullable = false)
    private boolean aware;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProgramDetails getProgramDetails() {
        return programDetails;
    }

    public void setProgramDetails(ProgramDetails programDetails) {
        this.programDetails = programDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAware() {
        return aware;
    }

    public void setAware(boolean aware) {
        this.aware = aware;
    }

    public SuggestedSupervisor withId(Integer id) {
        this.id = id;
        return this;
    }

    public SuggestedSupervisor withProgramDetails(ProgramDetails programDetails) {
        this.programDetails = programDetails;
        return this;
    }

    public SuggestedSupervisor withUser(User user) {
        this.user = user;
        return this;
    }

    public SuggestedSupervisor withAware(boolean aware) {
        this.aware = aware;
        return this;
    }

}
