/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Date;

/**
 *
 * @author theor
 */
public class CRDPLT {
    private String cardNumber;
    private String plateNumber;
    private Date datetimeIN;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Date getDatetimeIN() {
        return datetimeIN;
    }

    public void setDatetimeIN(Date datetimeIN) {
        this.datetimeIN = datetimeIN;
    }
}
