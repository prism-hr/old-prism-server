package com.zuehlke.pgadmissions.domain.resource;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.RESUME_RETIRED;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;

@Entity
@DiscriminatorValue("RESUME")
public class Resume extends Application {

    @Override
    public void setInstitution(Institution institution) {
        return;
    }

    @Override
    public void setDepartment(Department department) {
        return;
    }

    @Override
    public void setProgram(Program program) {
        return;
    }

    @Override
    public void setProject(Project project) {
        return;
    }

    public Resume withScope(PrismScope scope) {
        setScope(scope.name());
        return this;
    }

    public Resume withUser(User user) {
        setUser(user);
        return this;
    }

    public Resume withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
    }

    public Resume withOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        setOpportunityCategory(opportunityCategory);
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", getUser()).addProperty("opportunityCategory", getOpportunityCategory()).addExclusion("state.id", RESUME_RETIRED);
    }

}
