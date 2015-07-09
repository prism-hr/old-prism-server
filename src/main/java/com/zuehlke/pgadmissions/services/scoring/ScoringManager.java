package com.zuehlke.pgadmissions.services.scoring;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by felipe on 25/06/2015.
 *
 * This class is responsible for scoring the list of imported programs to
 * related subject areas
 */

public class ScoringManager {
    private ArrayList<ImportedProgram> lip; // list of imported programs
    private ArrayList<String> lsa; // list of subject areas
    private static Logger log = LoggerFactory.getLogger(ScoringManager.class);

    public ArrayList<String> getLsa() {
        return lsa;
    }

    public void setLsa(ArrayList<String> lsa) {
        this.lsa = lsa;
    }

    public ArrayList<ImportedProgram> getLip() {
        return lip;
    }

    public void setLip(ArrayList<ImportedProgram> lip) {
        this.lip = lip;
    }

    public void generateScoring() {
        for (int i = 0; i < lip.size(); i++) {
            ImportedProgram importedProgram = lip.get(i);
            importedProgram.calculateScoring(lsa, 0);
            log.info(lip.get(i).toString());

        }
    }
}
