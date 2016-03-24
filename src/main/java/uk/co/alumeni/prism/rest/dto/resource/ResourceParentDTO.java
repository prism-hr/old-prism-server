package uk.co.alumeni.prism.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ResourceParentDTO extends ResourceCreationDTO implements ResourceParentDefinition {

    @Valid
    private ResourceDTO parentResource;

    private String importedCode;

    @NotEmpty
    @Size(max = 255)
    private String name;

    @Size(max = 330)
    private String summary;

    @Size(max = 20000)
    private String description;

    @PhoneNumber
    private String telephone;

    @URL
    @Size(max = 2048)
    private String homepage;

    @Valid
    private List<ResourceConditionDTO> conditions;

    private List<PrismOpportunityCategory> opportunityCategories;

    @Valid
    private ResourceRelationCreationDTO target;

    private Integer targetInvitation;

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

    public String getImportedCode() {
        return importedCode;
    }

    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public List<ResourceConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public List<PrismOpportunityCategory> getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public ResourceRelationCreationDTO getTarget() {
        return target;
    }

    public void setTarget(ResourceRelationCreationDTO target) {
        this.target = target;
    }

    public Integer getTargetInvitation() {
        return targetInvitation;
    }

    public void setTargetInvitation(Integer targetInvitation) {
        this.targetInvitation = targetInvitation;
    }

}
