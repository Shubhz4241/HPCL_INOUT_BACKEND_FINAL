package com.hpcl.inout.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Mathadi")
public class Mathadi {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  
  @Column(unique = true, name = "uniqueId", length = 12)
  private String uniqueId;
  
  private String fullName;
  
  private String mobileNumber;
  
  private String address;
  
  private String  enumber;
  
  private String firmName;
  
  private String contractor;
  
  private String mtd;
  
  private String qr;
  
  @PostPersist
  private void Mathadi() {
      if (qr == null || qr.isBlank() || qr.equals("MT/HPNSK")) {
          this.qr = "MT/HPNSK/" + id;
      }
  }
  public Mathadi() {
    this.mtd = "Operation-Mathadi";
  }
 
public Mathadi(Long id, String uniqueId, String fullName, String mobileNumber, String address, String enumber,
		String firmName, String contractor, String mtd, String qr) {
	super();
	this.id = id;
	this.uniqueId = uniqueId;
	this.fullName = fullName;
	this.mobileNumber = mobileNumber;
	this.address = address;
	this.enumber = enumber;
	this.firmName = firmName;
	this.contractor = contractor;
	this.mtd = mtd;
	this.qr = qr;
}

public Long getId() {
	return id;
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
	return fullName;
}

public void setFullName(String fullName) {
	this.fullName = fullName;
}

public String getMobileNumber() {
	return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
}

public String getAddress() {
	return address;
}

public void setAddress(String address) {
	this.address = address;
}

public String getEnumber() {
	return enumber;
}

public void setEnumber(String enumber) {
	this.enumber = enumber;
}

public String getFirmName() {
	return firmName;
}

public void setFirmName(String firmName) {
	this.firmName = firmName;
}

public String getContractor() {
	return contractor;
}

public void setContractor(String contractor) {
	this.contractor = contractor;
}

public String getMtd() {
	return mtd;
}

public void setMtd(String mtd) {
	this.mtd = mtd;
}

public String getQr() {
	return qr;
}

public void setQr(String qr) {
	this.qr = qr;
}
 public String getMathadi() {
	return this.mtd;
	 
 }
 public void setMathadi(String mtd) {
	 this.mtd=mtd;
 }
}

