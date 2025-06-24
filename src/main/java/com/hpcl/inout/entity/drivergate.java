package com.hpcl.inout.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class drivergate {
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;
	  
	  private Long ofcid;
	  
	  private String qr;
	  
	  private String department;
	  
	  private String sub_department;
	  
	  private String truckNumber;
	  
	  private String firmName;
	  
	  private String name;
	  
	  private String status;
	  
	  @Column(name = "aadharNumber")
	  private String aadharNumber;
	  
	  private String mobile;
	  
	  private String address;
	  
	  @Temporal(TemporalType.TIMESTAMP)
	  private LocalDateTime entryDateTime;
	  
	  @Temporal(TemporalType.TIMESTAMP)
	  private LocalDateTime exitDateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOfcid() {
		return ofcid;
	}

	public void setOfcid(Long ofcid) {
		this.ofcid = ofcid;
	}


	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSub_department() {
		return sub_department;
	}

	public void setSub_department(String sub_department) {
		this.sub_department = sub_department;
	}

	

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	

	

	public LocalDateTime getEntryDateTime() {
		return entryDateTime;
	}

	public void setEntryDateTime(LocalDateTime entryDateTime) {
		this.entryDateTime = entryDateTime;
	}

	public LocalDateTime getExitDateTime() {
		return exitDateTime;
	}

	public void setExitDateTime(LocalDateTime exitDateTime) {
		this.exitDateTime = exitDateTime;
	}

	public drivergate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getTruckNumber() {
		return truckNumber;
	}

	public void setTruckNumber(String truckNumber) {
		this.truckNumber = truckNumber;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public drivergate(Long id, Long ofcid, String qr, String department, String sub_department, String truckNumber,
			String firmName, String name, String status, String aadharNumber, String mobile, String address,
			LocalDateTime entryDateTime, LocalDateTime exitDateTime) {
		super();
		this.id = id;
		this.ofcid = ofcid;
		this.qr = qr;
		this.department = department;
		this.sub_department = sub_department;
		this.truckNumber = truckNumber;
		this.firmName = firmName;
		this.name = name;
		this.status = status;
		this.aadharNumber = aadharNumber;
		this.mobile = mobile;
		this.address = address;
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;
	}

	

}
