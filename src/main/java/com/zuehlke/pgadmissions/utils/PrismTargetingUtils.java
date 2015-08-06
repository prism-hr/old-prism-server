package com.zuehlke.pgadmissions.utils;

public class PrismTargetingUtils {

    public static final String[] STOP_WORDS = new String[] { "study", "studies", "theory", "theories", "experience", "experiences", "student", "students",
            "development", "developments", "foundation", "foundations", "year", "years", "project", "projects", "abroad", "placement", "placements", "main",
            "mains", "top", "tops", "beginner", "beginners", "only", "applicant", "applicants", "new", "news", "joint", "joints", "extended", "integrated",
            "higher", "highers", "national", "diploma", "diplomas", "certificate", "certificates", "hnd", "hnc", "ba", "pathway", "pathways", "combined",
            "honour", "honours", "plus", "major", "majors" };

    public static boolean isValidUcasCodeFormat(String ucasCode) {
        return ucasCode.matches("^[A-Z]{1}[0-9]{3}$") || ucasCode.matches("^[A-Z]{2}[0-9]{2}$") || ucasCode.matches("^([A-Z]{1}[0-9]{1}){2}$");
    }

}
