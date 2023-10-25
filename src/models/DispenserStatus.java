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
public class DispenserStatus {
    private String uuid;
//    private String rfidNo; //cardNum
    private String device_id;
    private String gate_name; //entranceID
    private String status;
    private String statusMessage; //dateTimeINStamp
    private String dispenserStatus;  //plateNumber
   
    //data = entranceID + "," + cardNum + "," + plateNumber + "," + trtype + "," +
    //dateTimeINStamp + "," + isLost + "," + amountPaid + "," + paiddateIN;
 
    public DispenserStatus() {
        //plateNumbers = dbh.findAllPlatesfromVIPCard(cardFromReader);
        this.uuid = "";
        this.device_id = "";
        this.gate_name = "";
        this.status = "";
        this.statusMessage = "";
//        this.rfidNo = "";
        this.dispenserStatus = "";
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getGate_name() {
        return gate_name;
    }

    public void setGate_name(String gate_name) {
        this.gate_name = gate_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getDispenserStatus() {
        return dispenserStatus;
    }

    public void setDispenserStatus(String dispenserStatus) {
        this.dispenserStatus = dispenserStatus;
    }

}
