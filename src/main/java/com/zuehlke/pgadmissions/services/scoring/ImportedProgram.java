package com.zuehlke.pgadmissions.services.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by felipe on 25/06/2015. This is a temporary class until Alaister
 * finishes the refactor
 * <p/>
 * This class is used to map XML programs we've got from UCAS scrapper
 */
public class ImportedProgram {
    private String title;
    private String ucasId;
    private HashMap<Integer, ArrayList<String>> scoring;

    public ImportedProgram(String title, String ucasId) {
        // 4 places as we've got 4 partitions
        this.scoring = new HashMap<Integer, ArrayList<String>>(4);
        this.setTitle(title);
        this.setUcasId(ucasId);
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getUcasId() {
        return ucasId;
    }

    private void setUcasId(String ucasId) {
        this.ucasId = ucasId;
    }

    public void calculateScoring(ArrayList<String> subjectAreas, int i) {
        if (i == 4)
            return;
        int j = i + 1;
        calculateScoring(scan(title.substring(0, i + 1), subjectAreas, i), j);
    }

    private ArrayList<String> scan(final String code, final ArrayList<String> subjectSubset, final int index) {
        Iterator<String> it = subjectSubset.iterator();
        ArrayList<String> subList = new ArrayList<String>();
        while (it.hasNext()) {
            String subject = it.next();
            if (subject.startsWith(code)) {
                subList.add(subject);
                if (index > 0) {
                    scoring.get(index - 1).remove(subject);
                }
            }
        }
        scoring.put(index, subList);
        return new ArrayList<String>(subList);
    }

    @Override
    public String toString() {
        return "ImportedProgram{" +
                "title='" + title + '\'' +
                ", ucasId='" + ucasId + '\'' +
                ", scoring=" + scoring +
                '}';
    }

}
