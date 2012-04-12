package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;

public class ReferenceBuilder {

	private Integer id;
	
	private Document document;
	private Referee referee;
	
	public ReferenceBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReferenceBuilder referee(Referee referee){
		this.referee = referee;
		return this;
	}

	
	public ReferenceBuilder document(Document document){
		this.document = document;
		return this;
	}
	
	public Reference toReference(){
		Reference reference = new Reference();
		reference.setId(id);
		reference.setDocument(document);
		reference.setReferee(referee);
		return reference;
	}
}
