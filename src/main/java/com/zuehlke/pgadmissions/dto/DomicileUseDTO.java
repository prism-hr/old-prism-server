package com.zuehlke.pgadmissions.dto;

public class DomicileUseDTO {

    private String code;
    
    private Long useCount;

    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final Long getUseCount() {
        return useCount;
    }

    public final void setUseCount(Long useCount) {
        this.useCount = useCount;
    }
    
}