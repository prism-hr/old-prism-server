package uk.co.alumeni.prism.rest.representation.resource.application;

public class ApplicationBatchedDownloadRepresentation {

    private String uuid;

    int percentReady;

    public ApplicationBatchedDownloadRepresentation(String uuid, int percentReady) {
        this.uuid = uuid;
        this.percentReady = percentReady;
    }

    public ApplicationBatchedDownloadRepresentation() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPercentReady() {
        return percentReady;
    }

    public void setPercentReady(int percentReady) {
        this.percentReady = percentReady;
    }
}
