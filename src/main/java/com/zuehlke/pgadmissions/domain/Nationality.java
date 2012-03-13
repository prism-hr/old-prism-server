package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.NationalityType;

@Entity(name = "NATIONALITY")
@Access(AccessType.FIELD)
public class Nationality extends DomainObject<Integer> {

	private static final long serialVersionUID = 3334890054905085768L;
	
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;
	
	@Column(name = "primary_nationality")
	private boolean primary;
	
	
	@OneToMany	
	@JoinColumn(name = "nationality_id")
	private List<Document> supportingDocuments= new ArrayList<Document>();

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.NationalityTypeEnumUserType")
	@Column(name="nationality_type")
	private NationalityType nationalityType;
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}
	
	public void setCountry(Country country) {
		this.country = country;
		
	}

	public Country getCountry() {
		return country;
	}

	public void setSupportingDocuments(List<Document> supportingDocuments) {
		this.supportingDocuments = supportingDocuments;
	
		
	}

	public List<Document> getSupportingDocuments() {
		return supportingDocuments;
	}

	public void setType(NationalityType nationalityType) {
		this.nationalityType = nationalityType;
	
		
	}

	public NationalityType getType() {
		return nationalityType;
	}

	public String getAsJson() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("{\"type\": \"" + this.getType() + "\", \"country\": " + this.getCountry().getId() + ", \"supportingDocuments\": [");
		for(int i = 0; i < this.getSupportingDocuments().size(); i++){
			stringBuilder.append(this.getSupportingDocuments().get(i).getId());
			if(i < this.getSupportingDocuments().size() -1){
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("], \"primary\": \"" +  this.isPrimary() + "\"}");
		return stringBuilder.toString();
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	
}
