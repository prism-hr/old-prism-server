package uk.co.alumeni.prism.services.lifecycle.helpers;

import static org.joda.time.DateTime.now;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.AdvertService;

@Component
public class AdvertServiceHelperAdvertTargetPending extends PrismServiceHelperAbstract {

    @Inject
    private AdvertService advertService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        DateTime baseline = now();
        advertService.getAdvertTargetPendings().forEach(advertTargetPending -> {
            processAdvertTargetPending(advertTargetPending, baseline);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void processAdvertTargetPending(Integer advertTargetPending, DateTime baseline) {
        if (!isShuttingDown()) {
            advertService.processAdvertTargetPending(advertTargetPending, baseline);
        }
    }

}
