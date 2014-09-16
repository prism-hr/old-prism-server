package com.zuehlke.pgadmissions.dto;

public class AdvertCategoryImportRowDTO {

    private Integer id;

    private String name;

    public AdvertCategoryImportRowDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
}
