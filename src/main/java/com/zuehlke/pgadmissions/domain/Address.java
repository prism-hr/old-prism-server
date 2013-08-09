package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="ADDRESS")
public class Address implements Serializable {

	private static final long serialVersionUID = 2746228908173552617L;
	
	@Id
	@GeneratedValue
	private Integer id;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "country_id")
//	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "domicile_id")
	private Domicile domicile;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String address1;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String address2;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String address3;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String address4;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 12)
	private String address5;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
        this.id = id;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}
	
	public String getAddress5() {
		return address5;
	}
	
	public void setAddress5(String address5) {
		this.address5 = address5;
	}
	
	public String getLocationString() {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(address1)) {
			sb.append(address1);
		}
		if(StringUtils.isNotBlank(address2)) {
			sb.append("\n"+address2);
		}
		if(StringUtils.isNotBlank(address3)) {
			sb.append("\n"+address3);
		}
		if(StringUtils.isNotBlank(address4)) {
			sb.append("\n"+address4);
		}
		if(StringUtils.isNotBlank(address5)) {
			sb.append("\n"+address5);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getLocationString() + "\n" + (domicile != null ? domicile.getName() : "");
	}
}

