package uk.co.alumeni.prism.dto.json;

import java.util.List;

public class LocationSearchResponseDTO extends GoogleResponseDTO {

    private List<GoogleResultDTO> results;

    public List<GoogleResultDTO> getResults() {
        return results;
    }

    public void setResults(List<GoogleResultDTO> results) {
        this.results = results;
    }

}
