package com.hpcl.inout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Officer")
public class Officer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, name = "uniqueId", length = 12)
	private String uniqueId;

	private String fullName;

	private String mobileNumber;

	private String address;

	private String ofc;

	private String qr;

	@PostPersist
	  private void Officer() {
	      if (qr == null || qr.isBlank() || qr.equals("OFC/HPNSK")) {
	          this.qr = "OFC/HPNSK/" + id;
	      }
	  }
	
	public Officer() {
		this.ofc = "Operation-Officer";
	}

	public Officer(Long id, String uniqueId, String fullName, String mobileNumber, String address, String ofc,
			String qr) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.ofc = ofc;
		this.qr = qr;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOfc() {
		return this.ofc;
	}

	public void setOfc(String ofc) {
		this.ofc = ofc;
	}

	public String getQr() {
		return this.qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
}
