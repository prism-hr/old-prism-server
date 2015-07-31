package com.zuehlke.pgadmissions.dto.json;

public class EstablishmentSearchResponseDTO extends GoogleResponseDTO {

    private GoogleResultDTO result;

    public GoogleResultDTO getResult() {
        return result;
    }

    public void setResult(GoogleResultDTO result) {
        this.result = result;
    }

}
