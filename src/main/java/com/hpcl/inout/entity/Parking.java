package com.hpcl.inout.entity;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Parking")
public class Parking {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(unique = true, name = "parkingSlot", length = 12)
	private long parkingSlot;
	private String fullName;
	private String aadharNumber;
	private String vehicalType;
	private String vehicalNumber;
	private Date parkingDate;
	private Time timeIn;
	private boolean parkingStatus = true;
	public Parking() {
		super();
		// TODO Auto-generated constructor stub
	}
	// creates empty parking object
	public Parking emptyParking() {
		this.fullName = null;
		this.aadharNumber = null;
		this.vehicalType = null;
		this.vehicalNumber = null;
		this.timeIn = null;
		return this;
	}
	public Parking setParkingSlotForParking(Long parkingSlot) {
		System.out.println(parkingSlot);
		this.parkingSlot = parkingSlot;
		return this;
	}
	public Parking(long id, long parkingSlot, String fullName, String aadharNumber, String vehicalType,
			String vehicalNumber, Date parkingDate, Time timeIn, boolean parkingStatus) {
		super();
		this.id = id;
		this.parkingSlot = parkingSlot;
		this.fullName = fullName;
		this.aadharNumber = aadharNumber;
		this.vehicalType = vehicalType;
		this.vehicalNumber = vehicalNumber;
		this.parkingDate = parkingDate;
		this.timeIn = timeIn;
		this.parkingStatus = parkingStatus;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParkingSlot() {
		return parkingSlot;
	}
	public void setParkingSlot(long parkingSlot) {
		this.parkingSlot = parkingSlot;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public String getVehicalType() {
		return vehicalType;
	}
	public void setVehicalType(String vehicalType) {
		this.vehicalType = vehicalType;
	}
	public String getVehicalNumber() {
		return vehicalNumber;
	}
	public void setVehicalNumber(String vehicalNumber) {
		this.vehicalNumber = vehicalNumber;
	}
	public Date getParkingDate() {
		return parkingDate;
	}
	public void setParkingDate(Date parkingDate) {
		this.parkingDate = parkingDate;
	}
	public Time getTimeIn() {
		return timeIn;
	}
	public void setTimeIn(Time timeIn) {
		this.timeIn = timeIn;
	}
	public boolean isParkingStatus() {
		return parkingStatus;
	}
	public void setParkingStatus(boolean parkingStatus) {
		this.parkingStatus = parkingStatus;
	}
	@Override
	public String toString() {
		return "Parking [id=" + id + ", parkingSlot=" + parkingSlot + ", fullName=" + fullName + ", aadharNumber="
				+ aadharNumber + ", vehicalType=" + vehicalType + ", vehicalNumber=" + vehicalNumber + ", parkingDate=" + parkingDate
				+ ", timeIn=" + timeIn + ", parkingStatus=" + parkingStatus + "]";
	}

	
}
