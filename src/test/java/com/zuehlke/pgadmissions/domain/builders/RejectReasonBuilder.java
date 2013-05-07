package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.RejectReason;

public class RejectReasonBuilder {
	private String text;
	private Integer id;

	public RejectReasonBuilder text(String rejectionText) {
		this.text = rejectionText;
		return this;
	}

	public RejectReasonBuilder id(Integer reasonId) {
		this.id = reasonId;
		return this;
	}

	public RejectReason build() {
		RejectReason reason = new RejectReason();
		reason.setId(id);
		reason.setText(text);
		return reason;
	}
}
