package uk.co.alumeni.prism.rest.dto.advert;

import org.hibernate.validator.constraints.URL;
import uk.co.alumeni.prism.rest.dto.resource.ResourceConditionDTO;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

public class AdvertSettingsDTO {

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @Valid
    private List<ResourceConditionDTO> conditions;

    @Valid
    private AdvertVisibilityDTO visibility;

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public List<ResourceConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public AdvertVisibilityDTO getVisibility() {
        return visibility;
    }

    public void setVisibility(AdvertVisibilityDTO visibility) {
        this.visibility = visibility;
    }
}
