package com.hpcl.inout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Sec")
public class Sec {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, name = "uniqueId", length = 12)
	private String uniqueId;

	private String fullName;

	private String mobileNumber;

	private String address;

	private String firmName;

	private String sec;

	private String qr;

	@PostPersist
	  private void Sec() {
	      if (qr == null || qr.isBlank() || qr.equals("SEC/HPNSK")) {
	          this.qr = "SEC/HPNSK/" + id;
	      }
	  }
	
	public Sec() {
		this.sec = "Operation-Sec";
	}

	public Sec(Long id, String uniqueId, String fullName, String mobileNumber, String address, String firmName,
			String sec) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.firmName = firmName;
		this.sec = sec;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueId() {
		return this.uniqueId;
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

	public String getFirmName() {
		return this.firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getSec() {
		return this.sec;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

	public String getQr() {
		return this.qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
}
