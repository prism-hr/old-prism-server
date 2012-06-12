package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class DocumentTypeEnumUserType extends EnumUserType<DocumentType> {

	public DocumentTypeEnumUserType() {
		super(DocumentType.class);
	}
}