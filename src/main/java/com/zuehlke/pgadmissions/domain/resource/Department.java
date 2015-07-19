package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "department", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "title" }) })
public class Department extends ResourceParentDivision implements TargetEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "imported_code")
    private String importedCode;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "department_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(mappedBy = "department")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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
    public System getSystem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSystem(System system) {
        // TODO Auto-generated method stub

    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
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
        return null;
    }

    @Override
    public void setProject(Project project) {
        // TODO Auto-generated method stub

    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
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
        return resourceStates;
    }

    @Override
    public Set<ResourcePreviousState> getResourcePreviousStates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ResourceCondition> getResourceConditions() {
        return resourceConditions;
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
