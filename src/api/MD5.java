/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Theoretics
 */
public class MD5 {

    public static String md5Hash(byte[] data) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(data);
        String md5String = bytesToHex(result);
        return md5String.substring(12, 14).toUpperCase();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        MD5 m = new MD5();
        String s = new String("2902230420162214");
        String cs = m.md5Hash(s.getBytes());
        if (cs.length() < 2) {
            cs = "0" + cs;
        }
        System.out.println();
    }
}
