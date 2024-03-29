package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.SystemService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DocumentServiceHelperDelete extends PrismServiceHelperAbstract {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;

    @Inject
    private DocumentService documentService;

    @Inject
    private SystemService systemService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        DateTime baselineTime = new DateTime();
        documentService.deleteOrphanDocuments(baselineTime);
        if (amazonOn && systemService.getSystem().isDocumentExportEnabled()) {
            deleteAmazonDocuments(baselineTime);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void deleteAmazonDocuments(DateTime baselineTime) throws Exception {
        if (!isShuttingDown()) {
            documentService.deleteAmazonDocuments(baselineTime);
        }
    }

}
