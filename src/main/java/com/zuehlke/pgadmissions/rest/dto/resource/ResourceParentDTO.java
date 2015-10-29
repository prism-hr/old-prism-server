package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;

public class ResourceParentDTO extends ResourceCreationDTO implements ResourceParentDefinition<AdvertDTO> {

    @Valid
    private ResourceDTO parentResource;
    
    private String importedCode;

    @NotEmpty
    private String name;

    @Valid
    private AdvertDTO advert;

    private List<ResourceConditionDTO> conditions;

    private List<PrismOpportunityCategory> opportunityCategories;

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

    @Override
    public AdvertDTO getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
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
}
