/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;

/**
 *
 * @author Theoretics
 */
public class EntryRecord {
    private String uuid;
//    private String rfidNo; //cardNum
    private String entryQRCode;
    private String gateEntryID; //entranceID
    private String entrySessionID;
    private String entryDateTime; //dateTimeINStamp
    private String carPlateNo;  //plateNumber
    private String creditAmount; //amountPaid
    private String vehicleType; //trtype
    private String paymentAmount;
    private String dispenserImage;
    
//data = entranceID + "," + cardNum + "," + plateNumber + "," + trtype + "," +
    //dateTimeINStamp + "," + isLost + "," + amountPaid + "," + paiddateIN;
 
    public EntryRecord() {
        //plateNumbers = dbh.findAllPlatesfromVIPCard(cardFromReader);
        this.uuid = "";
        this.entrySessionID = "";
        this.entryDateTime = "";
        this.vehicleType = "";
        this.carPlateNo = "";
//        this.rfidNo = "";
        this.gateEntryID = "";
    }

    public String getCarPlateNo() {
        return carPlateNo;
    }

    public void setCarPlateNo(String carPlateNo) {
        this.carPlateNo = carPlateNo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEntrySessionID() {
        return entrySessionID;
    }

    public void setEntrySessionID(String entrySessionID) {
        this.entrySessionID = entrySessionID;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public String getEntryQRCode() {
        return entryQRCode;
    }

    public void setEntryQRCode(String entryQRCode) {
        this.entryQRCode = entryQRCode;
    }
    
    public String getEntryDateTime() {
        return entryDateTime;
    }

    public void setEntryDateTime(String entryDateTime) {
        this.entryDateTime = entryDateTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getGateEntryID() {
        return gateEntryID;
    }

    public void setGateEntryID(String gateEntryID) {
        this.gateEntryID = gateEntryID;
    }
    
    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getDispenserImage() {
        return dispenserImage;
    }

    public void setDispenserImage(String dispenserImage) {
        this.dispenserImage = dispenserImage;
    }
    
    
}
