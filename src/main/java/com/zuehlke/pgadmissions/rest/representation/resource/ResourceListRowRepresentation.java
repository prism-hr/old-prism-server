package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;

public class ResourceListRowRepresentation {

    private Integer id;

    private String code;

    private UserExtendedRepresentation user;

    private InstitutionRepresentation institution;

    private ProgramRepresentation program;

    private ProjectRepresentation project;

    private LocalDate closingDate;

    private PrismStateGroup state;

    private LocalDate dueDate;

    private Date displayTimestamp;

    private List<ActionRepresentation> actions;

    private BigDecimal averageRating;

    private String resourceScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ProgramRepresentation program) {
        this.program = program;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserExtendedRepresentation getUser() {
        return user;
    }

    public void setUser(UserExtendedRepresentation user) {
        this.user = user;
    }

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

    public ProjectRepresentation getProject() {
        return project;
    }

    public void setProject(ProjectRepresentation project) {
        this.project = project;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public PrismStateGroup getState() {
        return state;
    }

    public void setState(PrismStateGroup state) {
        this.state = state;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDisplayTimestamp() {
        return displayTimestamp;
    }

    public void setDisplayTimestamp(Date displayTimestamp) {
        this.displayTimestamp = displayTimestamp;
    }

    public List<ActionRepresentation> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentation> actions) {
        this.actions = actions;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public String getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(String resourceScope) {
        this.resourceScope = resourceScope;
    }

    public static class ProjectRepresentation {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class ProgramRepresentation {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
