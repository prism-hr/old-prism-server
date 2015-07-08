package com.zuehlke.pgadmissions.services.scrapping;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by felipe on 02/07/2015.
 */
public class ScrappingManager {
    ArrayList<ImportedSubjectArea> isa;
    public ScrappingManager() {
        isa = new ArrayList<ImportedSubjectArea>();
    }

    @Override
    public String toString() {
        return "ScrappingManager{" +
                "isa=" + isa +
                '}';
    }

    public ArrayList<ImportedSubjectArea> getIsa() {
        return isa;
    }

    public void setIsa(ArrayList<ImportedSubjectArea> isa) {
        this.isa = isa;
    }

    public ImportedSubjectArea addSubjectArea(ImportedSubjectArea currentSubjectArea, ImportedSubjectArea father) {
        if (father == null) {
            currentSubjectArea.setLevel(0);
        }
        currentSubjectArea.setFather(father);
        isa.add(currentSubjectArea);
        return currentSubjectArea;
    }
    public ImportedSubjectArea add(ImportedSubjectArea currentSubjectArea, ImportedSubjectArea father) {
       return father.route(currentSubjectArea, currentSubjectArea.getLevel());
    }

}
