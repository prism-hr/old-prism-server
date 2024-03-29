package uk.co.alumeni.prism.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DisplayPropertyConfigurationDTO extends WorkflowConfigurationDTO {

    @NotNull
    private PrismDisplayPropertyDefinition definitionId;

    @NotEmpty
    @Size(max = 1500)
    private String value;

    @Override
    public PrismDisplayPropertyDefinition getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(PrismDisplayPropertyDefinition definitionId) {
        this.definitionId = definitionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DisplayPropertyConfigurationDTO withValue(final String value) {
        this.value = value;
        return this;
    }

    public DisplayPropertyConfigurationDTO withDefinitionId(final PrismDisplayPropertyDefinition definitionId) {
        this.definitionId = definitionId;
        return this;
    }

}
