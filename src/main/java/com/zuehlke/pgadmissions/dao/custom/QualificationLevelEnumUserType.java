package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;



public class QualificationLevelEnumUserType extends EnumUserType<QualificationLevel> {

	public QualificationLevelEnumUserType(){
		super(QualificationLevel.class);
	}
}
