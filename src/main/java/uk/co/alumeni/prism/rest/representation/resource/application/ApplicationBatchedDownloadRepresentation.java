package uk.co.alumeni.prism.rest.representation.resource.application;

public class ApplicationBatchedDownloadRepresentation {
    
    private String uuid;
    
    private boolean ready;
    
    public String getUuid() {
        return uuid;
    }
    
    public ApplicationBatchedDownloadRepresentation setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public ApplicationBatchedDownloadRepresentation setReady(boolean ready) {
        this.ready = ready;
        return this;
    }
    
}
