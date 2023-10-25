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
public class Seniors {
    private String oscaName;
    private String oscaID;
    private String oscaAddr;
    private String oscaTIN;
    private String oscaBusStyle;

    public Seniors() {
        //plateNumbers = dbh.findAllPlatesfromVIPCard(cardFromReader);
        this.oscaID = "";
        this.oscaAddr = "";
        this.oscaTIN = "";
        this.oscaBusStyle = "";
        this.oscaName = "";
    }

    public String getOscaID() {
        return oscaID;
    }

    public void setOscaID(String oscaID) {
        this.oscaID = oscaID;
    }

    public String getOscaAddr() {
        return oscaAddr;
    }

    public void setOscaAddr(String oscaAddr) {
        this.oscaAddr = oscaAddr;
    }

    public String getOscaName() {
        return oscaName;
    }

    public void setOscaName(String oscaName) {
        this.oscaName = oscaName;
    }

    public String getOscaTIN() {
        return oscaTIN;
    }

    public void setOscaTIN(String oscaTIN) {
        this.oscaTIN = oscaTIN;
    }

    public String getOscaBusStyle() {
        return oscaBusStyle;
    }

    public void setOscaBusStyle(String oscaBusStyle) {
        this.oscaBusStyle = oscaBusStyle;
    }


}
