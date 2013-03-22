package com.zuehlke.pgadmissions.services;

import org.apache.commons.lang.BooleanUtils;
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
	
	public boolean hasSwitchedFromFalseToTrue(boolean newValueSetByTheUser) {
	    return BooleanUtils.isFalse(getThrottle().getEnabled()) && BooleanUtils.isTrue(newValueSetByTheUser);
	}
	
	public void updateThrottleWithNewValues(boolean enabled, String batchSize) throws NumberFormatException {
	    Throttle throttle = getThrottle();
        throttle.setEnabled(enabled);
        throttle.setBatchSize(Integer.parseInt(batchSize));
	}
	
	public void disablePorticoInterface() {
	    setPortioInterface(false);
	}
	
	public void enablePorticoInterface() {
	    setPortioInterface(true);
	}
	
	public boolean isPorticoInterfaceEnabled() {
	    return BooleanUtils.isTrue(getThrottle().getEnabled());
	}
	
	private void setPortioInterface(final boolean flag) {
	    Throttle throttle = getThrottle();
        throttle.setEnabled(flag);
	}
	
	public Throttle getThrottle() {
		return repository.get();
	}
}
