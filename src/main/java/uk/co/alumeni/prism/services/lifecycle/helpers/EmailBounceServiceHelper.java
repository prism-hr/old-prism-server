package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.services.EmailBounceService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EmailBounceServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private EmailBounceService emailBounceService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        processEmailBounces();
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void processEmailBounces() {
        if (!isShuttingDown()) {
            emailBounceService.processEmailBounces();
        }
    }

}
