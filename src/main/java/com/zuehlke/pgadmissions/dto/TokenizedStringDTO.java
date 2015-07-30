package com.zuehlke.pgadmissions.dto;

import java.util.Set;

public class TokenizedStringDTO {

    private Set<String> tokens;

    Integer uniqueTokenCount;

    public TokenizedStringDTO(Set<String> tokens, Integer uniqueTokenCount) {
        this.tokens = tokens;
        this.uniqueTokenCount = uniqueTokenCount;
    }

    public Set<String> getTokens() {
        return tokens;
    }

    public Integer getUniqueTokenCount() {
        return uniqueTokenCount;
    }

}
