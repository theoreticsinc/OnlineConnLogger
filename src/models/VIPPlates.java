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
public class VIPPlates {
    private String cardFromReader;
    private ArrayList<String> plateNumbers; //array2
    private ArrayList<String> vehicleTypes; //array1

    public VIPPlates() {
        this.cardFromReader = cardFromReader;
        //plateNumbers = dbh.findAllPlatesfromVIPCard(cardFromReader);
        this.plateNumbers = new ArrayList<String>();;
        this.vehicleTypes = new ArrayList<String>();;
    }

    public ArrayList<String> getPlateNumber() {
        return plateNumbers;
    }

    public ArrayList<String> getVehicleTypes() {
        return vehicleTypes;
    }

}
