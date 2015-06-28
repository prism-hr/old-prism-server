package com.zuehlke.pgadmissions.domain.department;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "department", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "title" }) })
public class Department extends ResourceParent implements TargetEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "title", nullable = false)
    private String title;

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public final void setId(Integer id) {
        this.id = id;
    }
    
    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public Department withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Department withTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public User getUser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUser(User user) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(String code) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public System getSystem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSystem(System system) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public final Institution getInstitution() {
        return institution;
    }

    @Override
    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }
    
    @Override
    public Department getDepartment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDepartment(Department department) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Program getProgram() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProgram(Program program) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Project getProject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProject(Project project) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Advert getAdvert() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAdvert(Advert advert) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Application getApplication() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State getState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setState(State state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public State getPreviousState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPreviousState(State previousState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LocalDate getDueDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DateTime getCreatedTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DateTime getUpdatedTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LocalDate getLastRemindedRequestIndividual() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastRemindedRequestIndividual(LocalDate lastRemindedRequestIndividual) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LocalDate getLastRemindedRequestSyndicated() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastRemindedRequestSyndicated(LocalDate lastRemindedRequestSyndicated) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LocalDate getLastNotifiedUpdateSyndicated() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastNotifiedUpdateSyndicated(LocalDate lastNotifiedUpdateSyndicated) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getWorkflowPropertyConfigurationVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowResourceConfigurationVersion) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSequenceIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<ResourceState> getResourceStates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ResourcePreviousState> getResourcePreviousStates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ResourceCondition> getResourceConditions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Comment> getComments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getUpdatedTimestampSitemap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getApplicationRatingCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setApplicationRatingCount(Integer applicationRatingCount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public BigDecimal getApplicationRatingFrequency() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public BigDecimal getApplicationRatingAverage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("institution", institution);
    }

}
