package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.SystemService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DocumentServiceHelperExport extends PrismServiceHelperAbstract {

    @Value("${integration.amazon.on}")
    private Boolean amazonOn;

    @Inject
    private DocumentService documentService;

    @Inject
    private SystemService systemService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        if (amazonOn && systemService.getSystem().isDocumentExportEnabled()) {
            List<Integer> documentIds = documentService.getExportDocuments();
            for (Integer documentId : documentIds) {
                exportDocumentToAmazon(documentId);
            }
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void exportDocumentToAmazon(Integer documentId) {
        if (!isShuttingDown()) {
            documentService.exportDocumentToAmazon(documentId);
        }
    }

}
