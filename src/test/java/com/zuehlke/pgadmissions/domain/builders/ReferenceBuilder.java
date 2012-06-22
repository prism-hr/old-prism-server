package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;

public class ReferenceBuilder {

	private Integer id;
	
	private Document document;
	private Referee referee;
	private Comment comment;
	private boolean suitableForUcl;
	private boolean suitableForProgramme;
	
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
	
	
	public ReferenceBuilder comment(Comment comment){
		this.comment = comment;
		return this;
	}
	
	public ReferenceBuilder suitableForUcl(boolean suitableForUcl){
		this.suitableForUcl = suitableForUcl;
		return this;
	}
	
	public ReferenceBuilder suitableForProgramme(boolean suitableForProgramme){
		this.suitableForProgramme = suitableForProgramme;
		return this;
	}
	
	public Reference toReference(){
		Reference reference = new Reference();
		reference.setId(id);
		reference.setDocument(document);
		reference.setReferee(referee);
		reference.setSuitableForProgramme(suitableForProgramme);
		reference.setSuitableForUCL(suitableForUcl);
		reference.setComment(comment);
		return reference;
	}
}
