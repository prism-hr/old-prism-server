package com.zuehlke.pgadmissions.services.scrapping;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by felipe on 02/07/2015.
 */
public class ImportedSubjectArea {
    private String code;
    private String name;
    private String description;
    private ArrayList<ImportedSubjectArea> siblings;
    private ImportedSubjectArea parent;
    private ImportedSubjectArea child;
    private int level = 0;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ImportedSubjectArea(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.siblings = new ArrayList<>();
        this.parent = null;
        this.child = null;

    }

    public ImportedSubjectArea addSi(ImportedSubjectArea c) {
        this.setFather(this);
        siblings.add(c);
        return c;
    }

    public ImportedSubjectArea route(ImportedSubjectArea node, int level) {
    int i = level > 0 ? level : 1;
        if (this.code.startsWith(node.getCode().substring(0, i))) {
            return this.addSibling(node, level);
        } else if (level == 3 && node.getCode().charAt(level) > this.getCode().charAt(level)) {
            return this.bubbleUp(node, level--);

        } else {
            return this.addChild(node, level++);
        }
    }

    public ImportedSubjectArea addSibling(ImportedSubjectArea node, int level) {
        node.setLevel(level);
        node.setFather(this.getParent());
        this.siblings.add(node);
        return node;
    }

    public ImportedSubjectArea addChild(ImportedSubjectArea node, int level) {
        node.setLevel(level++);
        node.setFather(this);
        this.child = node;
        return node;
    }

    public ArrayList<ImportedSubjectArea> getSiblings() {
        return siblings;
    }

    public void setSiblings(ArrayList<ImportedSubjectArea> siblings) {
        this.siblings = siblings;
    }

    public void setParent(ImportedSubjectArea parent) {
        this.parent = parent;
    }

    public ImportedSubjectArea getChild() {
        return child;
    }

    public void setChild(ImportedSubjectArea child) {
        this.child = child;
    }

    public ImportedSubjectArea bubbleUp(ImportedSubjectArea node, int level) {
        return this.getParent().route(node, level--);
    }

    public void setFather(ImportedSubjectArea f) {
        this.parent = f;
    }

    public ImportedSubjectArea getParent() {
        return parent;
    }

    public static ImportedSubjectArea readH3(Element h3) {
        String code = h3.text().substring(0, 1);
        String name = h3.text().substring(4, h3.text().length());
        return new ImportedSubjectArea(code, name, name);

    }

    public static ImportedSubjectArea readTrHead(Element tr) {
        String code = tr.getElementsByAttributeValue("width", "5%").text();
        String name = tr.getElementsByAttributeValue("width", "24%").text();
        String description = tr.getElementsByAttributeValue("width", "68%").text();
        return new ImportedSubjectArea(code, name, description);
    }

    public static ImportedSubjectArea readTrTail(Element tr) {
        String code = tr.children().get(2).text();
        String name = tr.children().get(3).text();
        String description = tr.children().get(4).text();
        return new ImportedSubjectArea(code, name, description);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ImportedSubjectArea{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", siblings=" + siblings +
                ", parent=" + parent +
                ", child=" + child +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
