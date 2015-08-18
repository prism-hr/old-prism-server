package com.zuehlke.pgadmissions.utils;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.zuehlke.pgadmissions.annotations.TargetingCalibrationSource;

public class PrismTargetingUtils {

    public static final String[] STOP_WORDS = new String[] { "study", "studies", "theory", "theories", "experience", "experiences", "student", "students",
            "development", "developments", "foundation", "foundations", "year", "years", "project", "projects", "abroad", "placement", "placements", "main",
            "mains", "top", "tops", "beginner", "beginners", "only", "applicant", "applicants", "new", "news", "joint", "joints", "extended", "integrated",
            "higher", "highers", "national", "diploma", "diplomas", "certificate", "certificates", "hnd", "hnc", "ba", "pathway", "pathways", "combined",
            "honour", "honours", "plus", "major", "majors" };

    // Data from: http://www.thecompleteuniversityguide.co.uk/league-tables/
    @TargetingCalibrationSource(subjectArea = 1, sources = { "Medicine", "Dentistry", "Nursing", "Anatomy & Physiology", "Aural & Oral Sciences",
            "Pharmacology & Pharmacy", "Complementary Medicine", "Opthalmics", "Medical Technology", "Physiotherapy" })
    @TargetingCalibrationSource(subjectArea = 2, sources = { "Biological Sciences", "Psychology", "Sports Science" })
    @TargetingCalibrationSource(subjectArea = 3, sources = { "Veterinary Medicine", "Agriculture & Forestry", "Food Science" })
    @TargetingCalibrationSource(subjectArea = 4, sources = { "Chemistry", "Materials Technology", "Physics & Astronomy", "Archaeology", "Geology",
            "Geography & Environmental Science" })
    @TargetingCalibrationSource(subjectArea = 5, sources = { "Mathematics" })
    @TargetingCalibrationSource(subjectArea = 6, sources = { "General Engineering", "Civil Engineering", "Mechanical Engineering",
            "Aeronautical & Manufacturing Engineering", "Chemical Engineering" })
    @TargetingCalibrationSource(subjectArea = 7, sources = { "Computer Science" })
    @TargetingCalibrationSource(subjectArea = 8, sources = { "Materials Technology", "Medical Technology" })
    @TargetingCalibrationSource(subjectArea = 9, sources = { "Architecture", "Building", "Town & Country Planning and Landscape Design" })
    @TargetingCalibrationSource(subjectArea = 10, sources = { "Economics", "Politics", "Sociology", "Social Policy", "Anthropology",
            "Geography & Environmental Science", "Social Work" })
    @TargetingCalibrationSource(subjectArea = 11, sources = { "Law" })
    @TargetingCalibrationSource(subjectArea = 12, sources = { "Accounting & Finance", "Business & Management Studies",
            "Hospitality, Leisure, Recreation & Tourism", "Land & Property Management", "Marketing" })
    @TargetingCalibrationSource(subjectArea = 13, sources = { "Communication & Media Studies", "Librarianship & Information Management" })
    @TargetingCalibrationSource(subjectArea = 14, sources = { "Celtic Studies", "Classics & Ancient History", "Linguistics", "English" })
    @TargetingCalibrationSource(subjectArea = 15, sources = { "French", "German", "Iberian Languages", "Italian", "Russian & East European Languages" })
    @TargetingCalibrationSource(subjectArea = 16, sources = { "American Studies", "East & South Asian Studies", "Middle Eastern & African Studies" })
    @TargetingCalibrationSource(subjectArea = 17, sources = { "History", "History of Art, Architecture & Design", "Philosophy", "Theology & Religious Studies" })
    @TargetingCalibrationSource(subjectArea = 18, sources = { "Art & Design", "Drama, Dance & Cinematics", "Philosophy", "Music" })
    @TargetingCalibrationSource(subjectArea = 19, sources = { "Education" })
    private static Multimap<Integer, Integer> topInstitutionsBySubjectArea = ArrayListMultimap.create();

    static {
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 2148, 6466, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 4083, 6721, 6908, 225219));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(5106, 6673, 6721, 6823, 7068));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(6466, 6610, 6668, 6673, 6938));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(4812, 6815, 6908, 6986, 7043));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 4089, 5133, 6560, 6610));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(600, 1702, 4812, 6725, 6743));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(325, 719, 1702, 5254, 225219));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(5094, 5133, 6701, 6815, 225175));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 5094, 5106, 6673, 6927));

        topInstitutionsBySubjectArea.putAll(2, newHashSet(1187, 2148, 6610, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(2, newHashSet(6466, 6560, 6610, 6721, 6938));
        topInstitutionsBySubjectArea.putAll(2, newHashSet(1187, 3045, 5106, 6560, 6701));

        topInstitutionsBySubjectArea.putAll(3, newHashSet(6610, 6721, 6823, 6927, 6938));
        topInstitutionsBySubjectArea.putAll(3, newHashSet(4089, 4227, 6908, 6927, 6986));
        topInstitutionsBySubjectArea.putAll(3, newHashSet(1009, 2741, 6815, 6927, 7068));

        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6610, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(2148, 4812, 6610, 6938, 7068));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6560, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 3033, 6585, 6610, 6938));

        topInstitutionsBySubjectArea.putAll(5, newHashSet(2148, 6610, 6938, 7053, 7176));

        topInstitutionsBySubjectArea.putAll(6, newHashSet(1187, 2148, 6610, 6668, 6938));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 5133, 6610, 6721, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(1951, 2148, 5112, 6560, 6610));

        topInstitutionsBySubjectArea.putAll(7, newHashSet(2148, 6585, 6610, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(8, newHashSet(2148, 4812, 6610, 6938, 7068));
        topInstitutionsBySubjectArea.putAll(8, newHashSet(5094, 5133, 6701, 6815, 225175));

        topInstitutionsBySubjectArea.putAll(9, newHashSet(719, 5112, 6560, 6610, 7025));
        topInstitutionsBySubjectArea.putAll(9, newHashSet(3045, 6466, 6823, 6986, 225219));
        topInstitutionsBySubjectArea.putAll(9, newHashSet(719, 1951, 6466, 6610, 6908));

        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(1187, 5106, 6560, 6610, 7068));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 5112, 6585, 6815, 6927));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(1187, 3033, 6585, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(2927, 5106, 6560, 6721, 7060));

        topInstitutionsBySubjectArea.putAll(11, newHashSet(1187, 3033, 6466, 6610, 6938));

        topInstitutionsBySubjectArea.putAll(12, newHashSet(2927, 5133, 6560, 6721, 7176));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3033, 3045, 6560, 7053, 7176));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3045, 3854, 5094, 5106, 7068));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3854, 4461, 5254, 6721, 6986));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(2927, 5133, 6560, 6815, 6908));

        topInstitutionsBySubjectArea.putAll(13, newHashSet(2927, 3045, 5133, 6815, 7025));
        topInstitutionsBySubjectArea.putAll(13, newHashSet(600, 1075, 3045, 4461, 220180));

        topInstitutionsBySubjectArea.putAll(14, newHashSet(719, 6610, 6721, 6938, 225175, 225250, 225256));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(1187, 6610, 6701, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(2927, 5112, 6466, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(1187, 6466, 6610, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6908, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6908, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6585, 6610, 6701, 225175));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6585, 6610, 6701, 6938));

        topInstitutionsBySubjectArea.putAll(16, newHashSet(5106, 6673, 7070, 7176, 225219));
        topInstitutionsBySubjectArea.putAll(16, newHashSet(5112, 6610, 6744, 6908, 6927, 6938));
        topInstitutionsBySubjectArea.putAll(16, newHashSet(1187, 5106, 5112, 6610, 7053));

        topInstitutionsBySubjectArea.putAll(17, newHashSet(1187, 6610, 6938, 7053, 7176));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(6466, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(2741, 3033, 6610, 6701, 6938));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(1187, 6610, 6701, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(18, newHashSet(2927, 3045, 5112, 6721, 6938));
        topInstitutionsBySubjectArea.putAll(18, newHashSet(5106, 6585, 6701, 7068, 7176));
        topInstitutionsBySubjectArea.putAll(18, newHashSet(1187, 5106, 6610, 6938, 225219));

        topInstitutionsBySubjectArea.putAll(19, newHashSet(1187, 6610, 6668, 6721, 7043));
    }

    public static boolean isValidUcasCodeFormat(String ucasCode) {
        return ucasCode.matches("^[A-Z]{1}[0-9]{3}$") || ucasCode.matches("^[A-Z]{2}[0-9]{2}$") || ucasCode.matches("^([A-Z]{1}[0-9]{1}){2}$");
    }

    public static Collection<Integer> getTopInstitutionsBySubjectArea(Integer subjectArea) {
        return topInstitutionsBySubjectArea.get(subjectArea);
    }

}
