package com.zuehlke.pgadmissions.services.scrapping;

import java.util.ArrayList;

/**
 * Created by felipe on 02/07/2015.
 */
public class ScrappingManager {
    ArrayList<ImportedSubjectAreaScraping> isa;

    public ScrappingManager() {
        isa = new ArrayList<ImportedSubjectAreaScraping>();
    }

    @Override
    public String toString() {
        return "ScrappingManager{" +
                "isa=" + isa +
                '}';
    }

    public ArrayList<ImportedSubjectAreaScraping> getIsa() {
        return isa;
    }

    public void setIsa(ArrayList<ImportedSubjectAreaScraping> isa) {
        this.isa = isa;
    }

    public ImportedSubjectAreaScraping addSubjectArea(ImportedSubjectAreaScraping currentSubjectArea, ImportedSubjectAreaScraping father) {
        if (father == null) {
            currentSubjectArea.setLevel(0);
        }
        currentSubjectArea.setFather(father);
        isa.add(currentSubjectArea);
        return currentSubjectArea;
    }

    public ImportedSubjectAreaScraping add(ImportedSubjectAreaScraping currentSubjectArea, ImportedSubjectAreaScraping father) {
        return father.route(currentSubjectArea, currentSubjectArea.getLevel());
    }

}
