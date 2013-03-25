package com.zuehlke.pgadmissions.services;

import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ThrottleDAO;
import com.zuehlke.pgadmissions.domain.Throttle;

@Service
@Transactional
public class ThrottleService {

	private final ThrottleDAO repository;

	@Autowired
    public ThrottleService(final ThrottleDAO repository) {
		this.repository = repository;
	}
	
	public ThrottleService() {
		this(null);
	}
	
	public int getBatchSize() {
	    return getThrottle().getBatchSize();
	}
	
	public boolean userTurnedOnThrottle(boolean newValueSetByTheUser) {
	    return isFalse(getThrottle().getEnabled()) && isTrue(newValueSetByTheUser);
	}
	
	public void updateThrottleWithNewValues(boolean enabled, String batchSize) throws NumberFormatException {
	    Throttle throttle = getThrottle();
        throttle.setEnabled(enabled);
        Integer size = Integer.parseInt(batchSize);
        if (size < 0) {
        	throw new NumberFormatException("Batch size cannot be negative");
        }
        throttle.setBatchSize(size);
	}
	
	public void disablePorticoInterface() {
	    setPortioInterface(false);
	}
	
	public void enablePorticoInterface() {
	    setPortioInterface(true);
	}
	
	public boolean isPorticoInterfaceEnabled() {
	    return isTrue(getThrottle().getEnabled());
	}
	
	private void setPortioInterface(final boolean flag) {
	    Throttle throttle = getThrottle();
        throttle.setEnabled(flag);
	}
	
	public Throttle getThrottle() {
		return repository.get();
	}
}
