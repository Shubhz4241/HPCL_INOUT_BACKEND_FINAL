package com.hpcl.inout.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bulk")          // lower‑case table name keeps things portable
public class Bulk {

    /* ---------- primary key ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ---------- business fields ---------- */
    @Column(name = "unique_id", length = 12, unique = true)
    private String uniqueId;

    private String fullName;
    private String mobileNumber;
    private String address;
    private String firmName;
    private String truckNumber;

    /** Role / category (“Driver‑Bulk” by default). */
    private String bulk = "Driver-Bulk";

    /** QR code string built from the database id:  BK/HPNSK/{id} */
    @Column(length = 50)
    private String qr;

   
    @PostPersist
    private void buildQr() {
        if (qr == null || qr.isBlank() || qr.equals("BK/HPNSK")) {
            this.qr = "BK/HPNSK/" + id;
        }
    }

    /* ---------- constructors ---------- */

    public Bulk() {
        // keep default values; qr is assigned in @PostPersist
    }

    public Bulk(Long id,
                String uniqueId,
                String fullName,
                String mobileNumber,
                String address,
                String firmName,
                String truckNumber) {
        this.id          = id;
        this.uniqueId    = uniqueId;
        this.fullName    = fullName;
        this.mobileNumber= mobileNumber;
        this.address     = address;
        this.firmName    = firmName;
        this.truckNumber = truckNumber;
        this.bulk        = "Driver-Bulk";
        this.qr          = "BK/HPNSK";   // will be completed in @PostPersist
    }

    /* ---------- getters & setters ---------- */

    public Long   getId()          { return id;          }
    public String getUniqueId()    { return uniqueId;    }
    public String getFullName()    { return fullName;    }
    public String getMobileNumber(){ return mobileNumber;}
    public String getAddress()     { return address;     }
    public String getFirmName()    { return firmName;    }
    public String getTruckNumber() { return truckNumber; }
    public String getBulk()        { return bulk;        }
    public String getQr()          { return qr;          }

    public void setId(Long id)                         { this.id = id; }
    public void setUniqueId(String uniqueId)           { this.uniqueId = uniqueId; }
    public void setFullName(String fullName)           { this.fullName = fullName; }
    public void setMobileNumber(String mobileNumber)   { this.mobileNumber = mobileNumber; }
    public void setAddress(String address)             { this.address = address; }
    public void setFirmName(String firmName)           { this.firmName = firmName; }
    public void setTruckNumber(String truckNumber)     { this.truckNumber = truckNumber; }
    public void setBulk(String bulk)                   { this.bulk = bulk; }
    public void setQr(String qr)                       { this.qr = qr; }
}
