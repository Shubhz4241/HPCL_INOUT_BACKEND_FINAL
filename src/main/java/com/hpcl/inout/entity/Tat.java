package com.hpcl.inout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tat")
public class Tat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, name = "uniqueId", length = 12)
	private String uniqueId;

	private String fullName;

	private String mobileNumber;

	private String address;

	private String tat;

	private String qr;

	@PostPersist
	  private void Tat() {
	      if (qr == null || qr.isBlank() || qr.equals("TAT/HPNSK")) {
	          this.qr = "TAT/HPNSK/" + id;
	      }
	  }
	
	public Tat() {
		this.tat = "Operation-Tat";
	}

	public Tat(Long id, String uniqueId, String fullName, String mobileNumber, String address, String tat) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.tat = tat;
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

	public String getTat() {
		return this.tat;
	}

	public void setTat(String tat) {
		this.tat = tat;
	}

	public String getQr() {
		return this.qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
}
