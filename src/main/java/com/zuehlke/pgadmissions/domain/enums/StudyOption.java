package com.zuehlke.pgadmissions.domain.enums;

public enum StudyOption {

    FLEXIBLE_STUDY_DISTANCE_LEARNING("Flexible study: distance learning"),

    FULL_TIME("Full-time"),

    FULL_TIME_EXCHANGE("Full-time: Exchange"),

    FULL_TIME_FULL_TIME_OFF_CAMPUS_THESIS_SUBMITED("Full-time: Full-time: off-campus thesis submited"),

    FULL_TIME_JYA_WITH_FEES("Full-time: JYA with Fees"),

    FULL_TIME_JUNIOR_YEAR_ABROAD("Full-time: Junior Year Abroad"),

    FULL_TIME_TECHNOLOGY_ENTERPRISE_SCHOLARSHIP_SCHM("Full-time: Technology Enterprise Scholarship Schm"),

    FULL_TIME_COMPLETING_RESEARCH("Full-time: completing research"),

    FULL_TIME_COMPLETING_RESEARCH_JOINT_REG_ELSEWHERE("Full-time: completing research joint reg elsewhere"),

    FULL_TIME_COMPLETING_RESEARCH_JOINT_REGISTRATION("Full-time: completing research joint registration"),

    FULL_TIME_COMPLETING_RESEARCH_OFF_CAMPUS_SCHEME("Full-time: completing research off-campus scheme"),

    FULL_TIME_DISTANCE_LEARNING("Full-time: distance learning"),

    FULL_TIME_EXTENSION_TO_COMPLETING_RESEARCH("Full-time: extension to completing research"),

    FULL_TIME_EXTENSN_TO_COMP_RESRCH_OFF_CAMPUS_SCHM("Full-time: extensn to comp resrch off-campus schm"),

    FULL_TIME_EXTRAMURAL_YEAR("Full-time: extramural year"),

    FULL_TIME_INCOMING_ERASMUS("Full-time: incoming Erasmus"),

    FULL_TIME_INCOMING_SOCRATES("Full-time: incoming Socrates"),

    FULL_TIME_JOINT_REGISTRATION("Full-time: joint registration"),

    FULL_TIME_JOINT_REGISTRATION_ELSEWHERE("Full-time: joint registration elsewhere"),

    FULL_TIME_NON_RESIDENT_RESEARCH("Full-time: non-resident research"),

    FULL_TIME_OFF_CAMPUS_SCHEME("Full-time: off-campus scheme"),

    FULL_TIME_REGISTERED_ELSEWHERE("Full-time: registered elsewhere"),

    FULL_TIME_REPEATING_PERIOD_OF_STUDY("Full-time: repeating period of study"),

    FULL_TIME_REPEATING_STUDY_JOINT_REG_ELSEWHERE("Full-time: repeating study joint reg elsewhere"),

    FULL_TIME_SANDWICH("Full-time: sandwich"),

    FULL_TIME_STUDY_ABROAD("Full-time: study abroad"),

    FULL_TIME_STUDY_LEAVE__RESEARCH_("Full-time: study leave (research)"),

    FULL_TIME_STUDY_LEAVE_JOINT_REG_ELSEWHERE("Full-time: study leave joint reg elsewhere"),

    FULL_TIME_STUDY_LEAVE_JOINT_REGISTRATION("Full-time: study leave joint registration"),

    FULL_TIME_STUDY_LEAVE_OFF_CAMPUS_SCHEME("Full-time: study leave off-campus scheme"),

    FULL_TIME_THESIS_SUBMITTED("Full-time: thesis submitted"),

    MODULAR_FLEXIBLE_STUDY("Modular/flexible study"),

    MODULAR_FLEXIBLE_STUDY_JOINT_REG_ELSEWHERE("Modular/flexible study: joint reg elsewhere"),

    MODULAR_FLEXIBLE_STUDY_JOINT_REGISTRATION("Modular/flexible study: joint registration"),

    MODULAR_FLEXIBLE_STUDY_REGISTERED_ELSEWHERE("Modular/flexible study: registered elsewhere"),

    NOT_IN_ATTENDANCE_WAS_FULL_TIME("Not in attendance: was Full-time"),

    NOT_IN_ATTENDANCE_WAS_FULL_TIME___JOINT_ELSEWHERE("Not in attendance: was Full-time - joint elsewhere"),

    NOT_IN_ATTENDANCE_WAS_FULL_TIME_DISTANCE_LEARNING("Not in attendance: was Full-time distance learning"),

    NOT_IN_ATTENDANCE_WAS_PART_TIME("Not in attendance: was Part-time"),

    NOT_IN_ATTENDANCE_WAS_PART_TIME_DISTANCE_LEARNING("Not in attendance: was Part-time distance learning"),

    NOT_IN_ATTENDANCE_WAS_FLEXIBLE_DISTANCE_LEARNING("Not in attendance: was flexible distance learning"),

    NOT_IN_ATTENDANCE_WAS_MOD_FLEX___JOINT_ELSEWHERE("Not in attendance: was mod/flex - joint elsewhere"),

    NOT_IN_ATTENDANCE_WAS_MOD_FLEX_JOINT_REG_ELSEWHR("Not in attendance: was mod/flex: joint reg elsewhr"),

    NOT_IN_ATTENDANCE_WAS_MODULAR_FLEXIBLE("Not in attendance: was modular/flexible"),

    NOT_IN_ATTENDANCE_WAS_PART_TIME___JOINT_ELSEWHERE("Not in attendance: was part-time - joint elsewhere"),

    PART_TIME("Part-time"),

    PART_TIME_EXCHANGE("Part-time: Exchange"),

    PART_TIME_JYA("Part-time: JYA"),

    PART_TIME_COMPLETING_RESEARCH("Part-time: completing research"),

    PART_TIME_COMPLETING_RESEARCH_NON_RESIDENT("Part-time: completing research non-resident"),

    PART_TIME_COMPLETING_RESEARCH_OFF_CAMPUS_SCHEME("Part-time: completing research off-campus scheme"),

    PART_TIME_COMPULSORY_STUDY_ABROAD("Part-time: compulsory study abroad"),

    PART_TIME_DISTANCE_LEARNING("Part-time: distance learning"),

    PART_TIME_EXTENSION_TO_COMPLETING_RESEARCH("Part-time: extension to completing research"),

    PART_TIME_EXTENSN_TO_COMP_RESRCH_OFF_CAMPUS_SCHM("Part-time: extensn to comp resrch off-campus schm"),

    PART_TIME_EXTRAMURAL_YEAR("Part-time: extramural year"),

    PART_TIME_JOINT_REGISTRATION("Part-time: joint registration"),

    PART_TIME_JOINT_REGISTRATION_ELSEWHERE("Part-time: joint registration elsewhere"),

    PART_TIME_NON_RESIDENT_RESEARCH("Part-time: non-resident research"),

    PART_TIME_OFF_CAMPUS_SCHEME("Part-time: off-campus scheme"),

    PART_TIME_OFF_CAMPUS__THESIS_SUBMITTED("Part-time: off-campus, thesis submitted"),

    PART_TIME_REGISTERED_ELSEWHERE("Part-time: registered elsewhere"),

    PART_TIME_REPEATING_PERIOD_OF_STUDY("Part-time: repeating period of study"),

    PART_TIME_REPEATING_STUDY_JOINT_REG_ELSEWHERE("Part-time: repeating study joint reg elsewhere"),

    PART_TIME_STUDY_LEAVE__RESEARCH_("Part-time: study leave (research)"),

    PART_TIME_THESIS_SUBMITTED("Part-time: thesis submitted"),

    UNION_SABBATICAL_OFFICER("Union Sabbatical Officer");

    private final String displayValue;

    private String freeVal;

    private StudyOption(String displayValue) {
        this.displayValue = displayValue;
        this.freeVal = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

    public String getFreeVal() {
        return freeVal;
    }

    public void setFreeVal(String freeVal) {
        this.freeVal = freeVal;
    }

    public static StudyOption fromString(String text) {
        if (text != null) {
            for (StudyOption b : StudyOption.values()) {
                if (text.equalsIgnoreCase(b.displayValue)) {
                    return b;
                }
            }
        }
        return null;
    }
}
