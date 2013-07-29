package com.zuehlke.pgadmissions.domain.enums;

public enum IeltsBandScores {

    FOUR("4"),
    FOUR_POINT_FIVE("4.5"),
    FIVE("5"),
    FIVE_POINT_FIVE("5.5"),
    SIX("6"),
    SIX_POINT_FIVE("6.5"),
    SEVEN("7"),
    SEVEN_POINT_FIVE("7.5"),
    EIGHT("8"),
    EIGHT_POINT_FIVE("8.5"),
    NINE("9");

    private final String score;
    
    private IeltsBandScores(String score) {
        this.score = score;
    }
    
    public String getDisplayValue() {
        return score;
    }
}
