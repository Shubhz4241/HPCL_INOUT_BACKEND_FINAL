package com.hpcl.inout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transportor")
public class Transportor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, name = "uniqueId", length = 12)
	private String uniqueId;

	private String fullName;

	private String mobileNumber;

	private String address;

	private String trans;
	
	private String firmName;

	@Column(unique = true, name = "truckNumber", length = 20)
	private String truckNumber;

	private String qr;

	public Transportor() {
		this.trans = "Driver-Transporter";
		this.qr = "TR/HPNSK";
	}
	
	public Transportor(Long id, String uniqueId, String fullName, String mobileNumber, String address, String trans) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.trans = trans;
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

	public String getTrans() {
		return this.trans;
	}

	public void setTrans(String trans) {
		this.trans = trans;
	}
	
	

	public String getTruckNumber() {
		return truckNumber;
	}

	public void setTruckNumber(String truckNumber) {
		this.truckNumber = truckNumber;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getQr() {
		return this.qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
}
