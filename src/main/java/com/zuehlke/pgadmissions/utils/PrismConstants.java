package com.zuehlke.pgadmissions.utils;

public class PrismConstants {

    public static final String OK = "OK";

    public static final String DOT = ".";

    public static final String SPACE = " ";

    public static final String ASTERIX = "*";

    public static final String WORD_BOUNDARY = "\\b";

    public static final String CHARACTER_WILDCARD = DOT + ASTERIX;

    public static final String ANGULAR_HASH = "#!";

    public static final String FILE_EXTENSION_PDF = "pdf";

    public static final Integer DEFAULT_RATING = 3;

    public static final Integer LIST_PAGE_ROW_COUNT = 50;

    public static final String SEQUENCE_IDENTIFIER = "sequenceIdentifier";

    public static final int MAX_BATCH_INSERT_SIZE = 1000;

    public static final String NULL = "null";

    public static final Integer RATING_PRECISION = 2;

    public static final Integer TARGETING_PRECISION = 9;

    public static final String REINITIALIZE_SERVER_MESSAGE_FOR_JUAN = "Likely what's happened here is that there are "
            + "some values in your database that are out of sync with the Java source. Try retarting your server with "
            + "the following flags set to true: 'startup.workflow.initialize', 'startup.display.initialize', "
            + "'startup.display.initialize.drop' ('environment.properties' file). You might need to check that you've "
            + "run any new SQL change scripts on your database. Try it out and let us know. Job's a good 'un!";

}
