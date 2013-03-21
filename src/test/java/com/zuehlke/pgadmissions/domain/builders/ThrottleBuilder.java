package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Throttle;

public class ThrottleBuilder {
	
	private Integer id;
	private Boolean enabled;
	private Integer batchSize;
	
	public ThrottleBuilder id(Integer id) {
		this.id=id;
		return this;
	}

	public ThrottleBuilder enabled(Boolean enabled) {
		this.enabled=enabled;
		return this;
	}
	
	public ThrottleBuilder batchSize(Integer batchSize) {
		this.batchSize=batchSize;
		return this;
	}
	
	public Throttle build() {
		Throttle throttle = new Throttle();
		throttle.setId(this.id);
		throttle.setEnabled(this.enabled);
		throttle.setBatchSize(this.batchSize);
		return throttle;
	}

}
