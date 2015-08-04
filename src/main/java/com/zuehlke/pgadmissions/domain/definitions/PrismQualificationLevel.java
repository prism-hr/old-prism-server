package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.Maps;

import java.util.Map;

public enum PrismQualificationLevel {

    POSTGRADUATE("Postgraduate"),
    UNDERGRADUATE("Undergraduate Degree"),
    OTHER("Other"),
    HE_LEVEL_2("HE Level 2 (HND's DipHEs and Foundation Degrees)"),
    HE_LEVEL_1("HE Level 1 (HNCs and CertHEs)"),
    HE_LEVEL_0("HE Level 0 (Foundation Year)");

    private String ucasLevel;

    private static Map<String, PrismQualificationLevel> byUcasLevel = Maps.newHashMap();

    static {
        for (PrismQualificationLevel level : values()) {
            byUcasLevel.put(level.getUcasLevel(), level);
        }
    }

    PrismQualificationLevel(String ucasLevel) {
        this.ucasLevel = ucasLevel;
    }

    public String getUcasLevel() {
        return ucasLevel;
    }

    public static PrismQualificationLevel getByUcasLevel(String ucasLevel) {
        return byUcasLevel.get(ucasLevel);
    }

}
