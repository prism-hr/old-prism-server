package com.zuehlke.pgadmissions.dto;

public class ApplicationExportConfigurationDTO {

    private boolean enabled;

    private int batchSize;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

}
