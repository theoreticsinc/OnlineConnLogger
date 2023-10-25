package api;

import misc.ImageDecoderEncoder;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import models.CONSTANTS;
import models.DispenserStatus;
import models.EntryRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONObject;
import org.apache.commons.codec.binary.Hex;

public class OnlineAuthConnection {

    public StringBuilder debugSB = new StringBuilder();
    public EntryRecord er = new EntryRecord();
    public ArrayList<DispenserStatus> dsAll = new ArrayList<DispenserStatus>();
    public String id_token;
    
    private static final Logger log = LogManager.getLogger(TestOnlineConn.class);
   

    public EntryRecord Auth() throws Exception {
        //EntryRecord er = new EntryRecord();       
        System.out.println("Authenticating to " + CONSTANTS.STESERVER + "/api/authenticate...");
        try {
            //URL url = new URL("http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/authenticate");
            URL url = new URL(CONSTANTS.STESERVER + "/api/authenticate");
            //52.221.203.242
//            final HttpConnectionParams httpParams = new HttpConnectionParams();
//            httpParams.setConnectionTimeout(30000);

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setReadTimeout(3000);
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");

            String data = "{" + "	\"username\": \"cashier\", \n" + "	\"password\": \"gp@20220606\"\n" + "}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            if (null == http) {
                return null;
            }
            OutputStream stream = http.getOutputStream();
            stream.write(out);

//            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            int responseCode = http.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                String result = sb.toString();
                System.out.println(result);

                JSONObject obj = new JSONObject(result);

                id_token = obj.getString("id_token");

            }

            http.disconnect();
        } catch (Exception ex) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex1) {
            }
        }
        return er;
    }

    public EntryRecord postNewTicket(String auth, String amount, String refID, String transactionID, String dateTime) {
        //EntryRecord er = new EntryRecord();     
        /*
        eException: Unparseable date: "2023-3-26T1:46:32.000+00:00"
        at java.text.DateFormat.parse(DateFormat.java:366)
        at api.OnlineAuthConnection.postNewTicket(OnlineAuthConnection.java:119)
        at UserInteface.HybridPanelUI.Create1MouseClicked(HybridPanelUI.java:5862)
        at UserInteface.HybridPanelUI.access$3600(HybridPanelUI.java:103)
        at UserInteface.HybridPanelUI$22.mouseClicked(HybridPanelUI.java:1682)
        at java.awt.AWTEventMulticaster.mouseClicked(AWTEventMulticaster.java:270)
        at java.awt.Component.processMouseEvent(Component.java:6542)
         */
        System.out.println("Posting Lost Ticket...");
        try {

            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Host");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Referer");
            System.setProperty("jdk.httpclient.redirects.retrylimit", "3");
            System.setProperty("jdk.httpclient.disableRetryConnect", "true");
            System.setProperty("jdk.httpclient.enableAllMethodRetry", "true");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Content-Length");
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
            System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
            System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");

            //URL url = new URL("http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/payment");
            URL url = new URL(CONSTANTS.STESERVER + "/api/v1/pos/penalty-ticket");
            //CONSTANTS.STESERVER +"/api/v1/pos/payment/
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Host", "52.221.203.242:5501");
            http.setRequestProperty("Accept", "*/*");
            http.setRequestProperty("Authorization", "Bearer " + auth);
            http.setRequestProperty("Content-Type", "application/json");

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
            final java.util.Date dateObj = sdf.parse(dateTime);
            dateObj.setHours(dateObj.getHours() - 16);

            String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(dateObj);
            System.out.println(aRevisedDate);

            String dat1a = "{"
                    //+ "    \"uuid\":  \"" + paymentUuid + "\",\n"
                    + "    \"amount\": " + amount + ",\n"
                    + "    \"currencyCode\": \"PHP\",\n"
                    + "    \"refID\": \"PHP" + refID + " \",\n"
                    + "    \"transactionType\": 1,\n"
                    + "    \"transactionID\": \"" + transactionID + "-" + amount + "\",\n"
                    + "    \"dateTime\":\"" + aRevisedDate + "\",\n"
                    + "    \"extendPayment\":true\n"
                    + "}";

            dat1a = "\"gateId\": 27,\n"
                    + "\"carPlateNo\": \"\",\n"
                    + "\"vehicleType\": \"car\",\n"
                    //                    + "\"entryDateTime\": \"2022-10-11T18:18:00.000Z\",\n"
                    + "\"entryDateTime\":\"" + aRevisedDate + "\",\n"
                    + "\"entrySessionId\": \"785632145820\",\n"
                    + "\"refID\": \"PHP" + refID + " \",\n"
                    + "\"currencyCode\": \"PHP\",\n"
                    + "\"paymentAmount\": " + amount + ",\n"
                    + "\"transactionType\": 1,\n"
                    + "\"transactionId\": \"TXN-" + transactionID + "\"";

//            System.out.println("Sending json to Payment API2 " + dat2a);
            System.out.println("Sending json to Penalty APIv1 \n" + dat1a);
            byte[] out = dat1a.getBytes(StandardCharsets.UTF_8);
//            long contentLen = 0L;

            //http.setHeader("Content-Length", Long.toString(contentLen));
            http.setRequestProperty("Content-Length", dat1a.toString().length() + "");

//
//            Map<String, List<String>> map = http.getHeaderFields();
//
//            for (String key : map.keySet()) {
//                System.out.println(key + ":");
//
//                List<String> values = map.get(key);
//
//                for (String aValue : values) {
//                    System.out.println("\t" + aValue);
//                }
//            }
            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            int responseCode = http.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                }

                bufferedReader.close();
                String result = sb.toString();
//                System.out.println(result);

                JSONObject obj = new JSONObject(result);

                String status = obj.getString("statusMessage");
                System.out.println("Payment SPI status:" + status);
            }

            System.out.println("HOST  " + http.getRequestProperty("Host"));
            System.out.println("Content-Length  " + http.getRequestProperty("Content-Length"));
            http.disconnect();
        } catch (Exception ex) {
//            Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return er;
    }

    public EntryRecord postPaymentSuccess(String auth, String paymentUuid, String amount, String refID, String transactionID, String dateTime) {
        //EntryRecord er = new EntryRecord();          
        System.out.println("Payment Posting...");
        try {

            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Host");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Referer");
            System.setProperty("jdk.httpclient.redirects.retrylimit", "3");
            System.setProperty("jdk.httpclient.disableRetryConnect", "true");
            System.setProperty("jdk.httpclient.enableAllMethodRetry", "true");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Content-Length");
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
            System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
            System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");

            //URL url = new URL("http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/payment");
            URL url = new URL(CONSTANTS.STESERVER + "/api/v1/pos/payment");
            //CONSTANTS.STESERVER +"/api/v1/pos/payment/
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Host", "52.221.203.242:5501");
            http.setRequestProperty("Accept", "*/*");
            http.setRequestProperty("Authorization", "Bearer " + auth);
            http.setRequestProperty("Content-Type", "application/json");

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
            final java.util.Date dateObj = sdf.parse(dateTime);
            dateObj.setHours(dateObj.getHours() - 16);

            String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(dateObj);
            System.out.println(aRevisedDate);

            String dat1a = "{"
                    + "    \"uuid\":  \"" + paymentUuid + "\",\n"
                    + "    \"amount\": " + amount + ",\n"
                    + "    \"currencyCode\": \"PHP\",\n"
                    + "    \"refID\": \"PHP" + refID + " \",\n"
                    + "    \"transactionType\": 1,\n"
                    + "    \"transactionID\": \"" + transactionID + "-" + amount + "\",\n"
                    + "    \"dateTime\":\"" + aRevisedDate + "\",\n"
                    + "    \"extendPayment\":true\n"
                    + "}";

//            System.out.println("Sending json to Payment API2 " + dat2a);
            System.out.println("Sending json to Payment APIv1 \n" + dat1a);
            byte[] out = dat1a.getBytes(StandardCharsets.UTF_8);
//            long contentLen = 0L;

            //http.setHeader("Content-Length", Long.toString(contentLen));
            http.setRequestProperty("Content-Length", dat1a.toString().length() + "");

//
//            Map<String, List<String>> map = http.getHeaderFields();
//
//            for (String key : map.keySet()) {
//                System.out.println(key + ":");
//
//                List<String> values = map.get(key);
//
//                for (String aValue : values) {
//                    System.out.println("\t" + aValue);
//                }
//            }
            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            int responseCode = http.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                }

                bufferedReader.close();
                String result = sb.toString();
//                System.out.println(result);

                JSONObject obj = new JSONObject(result);

                String status = obj.getString("statusMessage");
                System.out.println("Payment SPI status:" + status);
            }

            System.out.println("HOST  " + http.getRequestProperty("Host"));
            System.out.println("Content-Length  " + http.getRequestProperty("Content-Length"));
            http.disconnect();
        } catch (Exception ex) {
//            Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return er;
    }

    public EntryRecord Inquire(String auth, String rfidNum) {
        HttpURLConnection urlConnection;
        //EntryRecord er = new EntryRecord();
        try {
            URL url = new URL("http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/enquiry");
            //Connect
            urlConnection = (HttpURLConnection) ((url.openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
//            System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
//                    System.out.println(line);
                    debugSB.append(line + "\n");
                }

                bufferedReader.close();
                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                org.json.JSONArray arr = obj.getJSONArray("items");
                for (int i = 0; i < arr.length(); i++) {
                    String uuid = arr.getJSONObject(i).getString("uuid");
                    er.setUuid(uuid);
                    String entrySessionID = arr.getJSONObject(i).getString("entrySessionID");
                    er.setEntrySessionID(entrySessionID);
                    int gateEntryID = arr.getJSONObject(i).getInt("gateEntryID");
                    er.setGateEntryID(gateEntryID + "");
                    int creditAmount = arr.getJSONObject(i).getInt("creditAmount");
                    er.setCreditAmount(creditAmount + "");
                    String vehicleType = arr.getJSONObject(i).getString("vehicleType");
                    er.setVehicleType("R");
                    String carPlateNo = arr.getJSONObject(i).getString("carPlateNo");
                    er.setCarPlateNo(carPlateNo);
                    String entryDateTime = arr.getJSONObject(i).getString("entryDateTime");

                    System.out.println("uuid:" + uuid);
                    System.out.println("entrySessionID:" + entrySessionID);
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");  

                    //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //String entry = sdf.format(entryDateTime);
                    //System.out.println("Entry DT:" + entry);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
                    final java.util.Date dateObj = sdf.parse(entryDateTime);

                    String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss.SSS").format(dateObj);
                    System.out.println(aRevisedDate);
                    er.setEntryDateTime(aRevisedDate);
                    //if (rfidNum.compareTo(rfidNo) == 0) {
                    return er;
                    //}
                }
//                System.out.println(result);
            }
        } catch (Exception ex) {
//            Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return er;
    }

    public EntryRecord SearchVehicle(String auth, String carPlateNo) {
        HttpURLConnection urlConnection;
        System.out.println("SearchVehicle: " + carPlateNo);
        //EntryRecord er = new EntryRecord();
        try {
            ///pos/enquiry?rfidNo=01090516472177&startDateTime={{startDateTime}}&endDateTime={{endDateTime}}
            //String connectionURL = "http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/enquiry?entryQRCode=" +entryQRCode;
//            entryQRCode = entryQRCode + this.crc8(StringToHexadecimal(entryQRCode.toString()).getBytes());
            String connectionURL = CONSTANTS.STESERVER + "/api/v1/pos/search-vehicle?carPlateNo=" + carPlateNo;
            System.out.println("Connection URL:" + connectionURL);
            URL url = new URL(connectionURL);
            //Connect
            urlConnection = (HttpURLConnection) ((url.openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                    log.debug(line);
                    debugSB.append(line + "\n");
                }

                bufferedReader.close();
                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                org.json.JSONArray arr = obj.getJSONArray("parkingSession");
                for (int i = 0; i < arr.length(); i++) {
                    String uuid = arr.getJSONObject(i).getString("uuid");
                    er.setUuid(uuid);
                    er.setEntryQRCode(carPlateNo);
                    String entrySessionID = arr.getJSONObject(i).getString("entry_session_id");
                    er.setEntrySessionID(entrySessionID);
                    int gateEntryID = arr.getJSONObject(i).getInt("gate_entry_id");
                    er.setGateEntryID(gateEntryID + "");
                    int creditAmount = arr.getJSONObject(i).getInt("credit_amount");
                    er.setCreditAmount(creditAmount + "");
                    String vehicleType = arr.getJSONObject(i).getString("vehicle_type");
                    er.setVehicleType("R");
                    //String carPlateNo = arr.getJSONObject(i).getString("vehicle_plate_no");
                    er.setCarPlateNo(carPlateNo);
                    String entryDateTime = arr.getJSONObject(i).getString("entry_date_time");
                    System.out.println("uuid:" + uuid);
                    System.out.println("entrySessionID:" + entrySessionID);
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");  

                    //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //String entry = sdf.format(entryDateTime);
                    //System.out.println("Entry DT:" + entry);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
                    final java.util.Date dateObj = sdf.parse(entryDateTime);

                    String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                    System.out.println(aRevisedDate);
//                    int hourAdj = dateObj.getHours() + 8;
//                    dateObj.setHours(hourAdj);
                    aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
                    System.out.println(aRevisedDate);
                    er.setEntryDateTime(aRevisedDate);
                    if (uuid != null || uuid.compareTo("") != 0) {
                        return er;
                    }
                }
//                System.out.println(result);
//DUMMY404
            } else {
                return null;
            }
        } catch (Exception ex) {
            //Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return null;
        }
        return er;
    }

    public EntryRecord CreateLostQRTicket(String auth, String carPlateNo) {
        HttpURLConnection urlConnection;
        System.out.println("SearchVehicle: " + carPlateNo);
        //EntryRecord er = new EntryRecord();
        try {
            ///pos/enquiry?rfidNo=01090516472177&startDateTime={{startDateTime}}&endDateTime={{endDateTime}}
            //String connectionURL = "http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/enquiry?entryQRCode=" +entryQRCode;
//            entryQRCode = entryQRCode + this.crc8(StringToHexadecimal(entryQRCode.toString()).getBytes());
            String connectionURL = CONSTANTS.STESERVER + "/api/v1/pos/penalty-ticket?carPlateNo=" + carPlateNo;
            System.out.println("Connection URL:" + connectionURL);
            URL url = new URL(connectionURL);
            //Connect
            urlConnection = (HttpURLConnection) ((url.openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                    debugSB.append(line + "\n");
                }

                bufferedReader.close();
                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                org.json.JSONArray arr = obj.getJSONArray("parkingSession");
                for (int i = 0; i < arr.length(); i++) {
                    String uuid = arr.getJSONObject(i).getString("uuid");
                    er.setUuid(uuid);
                    er.setEntryQRCode(carPlateNo);
                    String entrySessionID = arr.getJSONObject(i).getString("entry_session_id");
                    er.setEntrySessionID(entrySessionID);
                    int gateEntryID = arr.getJSONObject(i).getInt("gate_entry_id");
                    er.setGateEntryID(gateEntryID + "");
                    int creditAmount = arr.getJSONObject(i).getInt("credit_amount");
                    er.setCreditAmount(creditAmount + "");
                    String vehicleType = arr.getJSONObject(i).getString("vehicle_type");
                    er.setVehicleType("R");
                    //String carPlateNo = arr.getJSONObject(i).getString("vehicle_plate_no");
                    er.setCarPlateNo(carPlateNo);
                    String entryDateTime = arr.getJSONObject(i).getString("entry_date_time");
                    System.out.println("uuid:" + uuid);
                    System.out.println("entrySessionID:" + entrySessionID);
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");  

                    //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //String entry = sdf.format(entryDateTime);
                    //System.out.println("Entry DT:" + entry);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
                    final java.util.Date dateObj = sdf.parse(entryDateTime);

                    String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                    System.out.println(aRevisedDate);
//                    int hourAdj = dateObj.getHours() + 8;
//                    dateObj.setHours(hourAdj);
                    aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
                    System.out.println(aRevisedDate);
                    er.setEntryDateTime(aRevisedDate);
                    if (uuid != null || uuid.compareTo("") != 0) {
                        return er;
                    }
                }
//                System.out.println(result);
//DUMMY404
            } else {
                return null;
            }
        } catch (Exception ex) {
            //Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return null;
        }
        return er;
    }

    public EntryRecord InquireSpecific(String auth, String entryQRCode) {
        HttpURLConnection urlConnection;
        System.out.println("Inquiring Specific qr: " + entryQRCode);
        //EntryRecord er = new EntryRecord();
        try {
            ///pos/enquiry?rfidNo=01090516472177&startDateTime={{startDateTime}}&endDateTime={{endDateTime}}
            //String connectionURL = "http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/enquiry?entryQRCode=" +entryQRCode;
//            entryQRCode = entryQRCode + this.crc8(StringToHexadecimal(entryQRCode.toString()).getBytes());
            String connectionURL = CONSTANTS.STESERVER + "/api/v1/pos/enquiry?entryQRCode=" + entryQRCode;
            System.out.println("Connection URL:" + connectionURL);
            URL url = new URL(connectionURL);
            //Connect
            urlConnection = (HttpURLConnection) ((url.openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                    debugSB.append(line + "\n");
                }
//                processJSON(debugSB.toString());
                bufferedReader.close();
                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                org.json.JSONArray arr = obj.getJSONArray("parkingSession");
                Random random = new Random();
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 150;
                String dispImg = "";
                try {
                    dispImg = obj.getString("plateImage");
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                DataBaseHandler dbh = new DataBaseHandler();
//                String compareSnapshot = dbh.findSnapshotImage(entryQRCode);
//                if (null != compareSnapshot) {
//                    dispImg = compareSnapshot;
//                } else {
//                    int min = 1;
//                    int max = 10;
//                    int randomNumber = (int) (Math.random() * (max - min)) + min;
//                    //if (randomNumber % 2 == 0) {
//                    String dispNewImg = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
//                    //}
//                    ImageDecoderEncoder ide = new ImageDecoderEncoder();
//                    BufferedImage buf = ide.decodeToImage(dispImg);
//                    String rS = ide.rollingSnapshots(buf);
//                    //String rS = ide.encodeToString(buf, "jpg");
////                    ide.convertNSaveString2BuffImage(rS);
//                    dbh.saveSnapshotImage(entryQRCode, rS);
//                }
                try {
                    er.setDispenserImage(dispImg);
                } catch (Exception e) {

                }
                for (int i = 0; i < arr.length(); i++) {
                    String uuid = arr.getJSONObject(i).getString("uuid");
                    er.setUuid(uuid);
                    er.setEntryQRCode(entryQRCode);
                    String entrySessionID = arr.getJSONObject(i).getString("entry_session_id");
                    er.setEntrySessionID(entrySessionID);
                    int gateEntryID = arr.getJSONObject(i).getInt("gate_entry_id");
                    er.setGateEntryID(gateEntryID + "");
                    int creditAmount = arr.getJSONObject(i).getInt("credit_amount");
                    er.setCreditAmount(creditAmount + "");
                    String vehicleType = arr.getJSONObject(i).getString("vehicle_type");
                    er.setVehicleType("R");
                    String carPlateNo = arr.getJSONObject(i).getString("vehicle_plate_no");
                    er.setCarPlateNo(carPlateNo);

                    String entryDateTime = arr.getJSONObject(i).getString("entry_date_time");
//                    System.out.println("uuid:" + uuid);
//                    System.out.println("entrySessionID:" + entrySessionID);
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");  

                    //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //String entry = sdf.format(entryDateTime);
                    //System.out.println("Entry DT:" + entry);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
                    final java.util.Date dateObj = sdf.parse(entryDateTime);

                    String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                    System.out.println(aRevisedDate);
//                    int hourAdj = dateObj.getHours() + 8;
//                    dateObj.setHours(hourAdj);
                    aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                    System.out.println(aRevisedDate);
                    er.setEntryDateTime(aRevisedDate);
                    if (uuid != null || uuid.compareTo("") != 0) {

                        return er;
                    }
                }

//                System.out.println(result);
//DUMMY404
            } else if (responseCode == 404) {
                System.out.println("entryQRCode:" + entryQRCode);
                //AAGGYYMMDDHHMMSSCS
//                String areaID = entryQRCode.substring(0, 2);
//                String gateID = entryQRCode.substring(2, 4);
//                String year = entryQRCode.substring(4, 6);
//                String month = entryQRCode.substring(6, 8);
//                String date = entryQRCode.substring(8, 10);
//                String hour = entryQRCode.substring(10, 12);
//                String minute = entryQRCode.substring(12, 14);
//                String seconds = entryQRCode.substring(14, 16);
//
//                er.setUuid("12312345");
//                er.setEntryQRCode(entryQRCode);
//                er.setVehicleType("R");
//                er.setGateEntryID("001");
//                er.setCarPlateNo("");
//                //2022-11-07 13:47:29.000
//                String entryDateTime = "2023-03-07T01:30:05.000+00:00";
//                entryDateTime = "20" + year + "-" + month + "-" + date + "T" + hour + ":" + minute + ":" + seconds + ".000+00:00";
//                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
//                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
//                final java.util.Date dateObj = sdf.parse(entryDateTime);
//
//                String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
//                System.out.println(aRevisedDate);
//                er.setEntryDateTime(aRevisedDate);
            } else {
                return null;
            }
        } catch (Exception ex) {
            //Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return null;
        }
        return er;
    }

    public ArrayList<DispenserStatus> getDispenserStatus(String auth) {
        HttpURLConnection urlConnection;
        System.out.println("Dispenser Status: ");
        //EntryRecord er = new EntryRecord();
        try {
            ///pos/enquiry?rfidNo=01090516472177&startDateTime={{startDateTime}}&endDateTime={{endDateTime}}
            //String connectionURL = "http://a401acbb94c774d0eb1a29bc91408afa-1199273121.ap-southeast-1.elb.amazonaws.com/api/v1/pos/enquiry?entryQRCode=" +entryQRCode;
//            entryQRCode = entryQRCode + this.crc8(StringToHexadecimal(entryQRCode.toString()).getBytes());
            String connectionURL = CONSTANTS.STESERVER + "/api/v1/pos/get-dispenser-status";
            System.out.println("Connection URL:" + connectionURL);
            URL url = new URL(connectionURL);
            //Connect
            urlConnection = (HttpURLConnection) ((url.openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + auth);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                    debugSB.append(line + "\n");
                }

                bufferedReader.close();
                String result = sb.toString();

                JSONObject obj = new JSONObject(result);
                org.json.JSONArray arr = obj.getJSONArray("dispenserStatus");
                for (int i = 0; i < arr.length(); i++) {
                    DispenserStatus ds = new DispenserStatus();
                    String device_id = arr.getJSONObject(i).getString("device_id");
                    ds.setDevice_id(device_id);
                    String gate_name = arr.getJSONObject(i).getString("gate_name");
                    ds.setGate_name(gate_name);
                    String status = arr.getJSONObject(i).getString("status");
                    ds.setStatus(status);
//                    if (device_id != null || device_id.compareTo("") != 0) {
//                        return ds;
//                    }
                    dsAll.add(ds);
                }
//                System.out.println(result);
//DUMMY404
            } else if (responseCode == 404) {
                System.out.println("NO Dispenser in system:");

            } else {
                return null;
            }
        } catch (Exception ex) {
            //Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return null;
        }
        return dsAll;
    }

    public String crc8(byte[] data) {
        final int poly = 0xD5;
        int _crc = 0;
        for (int i = 0; i < data.length; i++) {
            _crc = crc8(data[i], _crc, poly);
        }
        System.out.println("crc:" + Integer.toHexString(_crc).toUpperCase());
        return Integer.toHexString(_crc).toUpperCase();
    }

    private int crc8(final byte b, int crc, int poly) {
        crc ^= b;
        for (int j = 0; j < 8; j++) {
            if ((crc & 0x80) != 0) {
                crc = ((crc << 1) ^ poly);
            } else {
                crc <<= 1;
            }
        }
        return crc &= 0xFF;
    }

    public String StringToHexadecimal(String args) {
        System.out.println("Enter a String value: ");
        String str = args;
        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        System.out.println(result);
        return result;
    }

    public char[] convertStringToHex(String str) {

        // display in uppercase
        //char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8), false);
        // display in lowercase, default
        char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < chars.length; i++) {
            System.out.println(chars[i]);
        }
        return chars;
    }

    public BufferedImage convertStringFromFile2Image(String fileName) {
        BufferedImage img = null;
        try {
            try {
                String content = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException ex) {
//                Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fileName);
            } catch (FileNotFoundException ex) {
//                Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] buffer = new byte[10];
            StringBuilder sb = new StringBuilder();
            while (fis.read(buffer) != -1) {
                sb.append(new String(buffer));
                buffer = new byte[10];
            }
            fis.close();

            String base64String = sb.toString();
            String[] strings = base64String.split(",");
            String extension;
            switch (strings[0]) {//check image's extension
                case "data:image/jpeg;base64":
                    extension = "jpeg";
                    break;
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default://should write cases for more images types
                    extension = "jpg";
                    break;
            }
            //convert base64 string to binary data
            byte[] data = DatatypeConverter.parseBase64Binary(base64String);

            InputStream is = new ByteArrayInputStream(data);
            img = ImageIO.read(is);

            Path target = Paths.get("c:\\NETSHARE\\new.png");
            Graphics2D g = img.createGraphics();
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("Plate", 100, 100);

            // save it
            ImageIO.write(img, "png", target.toFile());
//            String path = "C:\\NETSHARE\\test_imageX." + extension;
//            File file = new File(path);
//            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
//                outputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File outputfile = new File("C:\\NETSHARE\\test_imageX.jpg");
//            ImageIO.write(img, "jpg", outputfile);

        } catch (Exception ex) {
//            Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return img;
    }

    private static String testResult = "{\n"
            + "  \"status\" : 1,\n"
            + "  \"statusMessage\" : \"1 session(s) found\",\n"
            + "  \"parkingSession\" : [ {\n"
            + "    \"id\" : 910,\n"
            + "    \"uuid\" : \"cb8940c5-c1c3-406d-ac6d-815aa529611f\",\n"
            + "    \"car_park_id\" : 3001,\n"
            + "    \"vehicle_plate_no\" : \"NBB8704\",\n"
            + "    \"vehicle_id\" : 0,\n"
            + "    \"vehicle_type\" : \"car\",\n"
            + "    \"entry_date_time\" : \"2023-08-22T05:48:59.000+00:00\",\n"
            + "    \"entry_session_id\" : \"2901230822134858BB\",\n"
            + "    \"entry_type\" : \"hourly\",\n"
            + "    \"exit_date_time\" : null,\n"
            + "    \"exit_session_id\" : null,\n"
            + "    \"exit_type\" : null,\n"
            + "    \"expiry_date_time\" : null,\n"
            + "    \"allow_open\" : 0,\n"
            + "    \"ref_id\" : null,\n"
            + "    \"currency_code\" : null,\n"
            + "    \"credit_amount\" : 0,\n"
            + "    \"payment_amount\" : 0,\n"
            + "    \"transaction_amount\" : 0,\n"
            + "    \"duration\" : 0,\n"
            + "    \"driver_code\" : null,\n"
            + "    \"noti_contact\" : null,\n"
            + "    \"parking_type_id\" : 2,\n"
            + "    \"transaction_type\" : 0,\n"
            + "    \"transaction_id\" : null,\n"
            + "    \"company_code\" : null,\n"
            + "    \"status\" : \"new\",\n"
            + "    \"created_by\" : \"TransProxy\",\n"
            + "    \"created_date\" : \"2023-08-22T05:48:59.000+00:00\",\n"
            + "    \"last_modified_by\" : null,\n"
            + "    \"last_modified_date\" : null,\n"
            + "    \"iu_number\" : \"\",\n"
            + "    \"gate_entry_id\" : 29,\n"
            + "    \"gate_exit_id\" : 0,\n"
            + "    \"parked_exceed_quota\" : false,\n"
            + "    \"synced\" : false\n"
            + "  } ],\n"
            + "  \"intercomImage\" : \"/9j/4AAQSkZJRgABAQEAAAAAAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHhofHx0aHRwhJS8oISMsIxwdKTgpLDAyNTU1ICc6PjkzPS80NTL/2wBDAQkJCQwLDBgNDRgyIh0iMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAEgAWADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDzWBMsK6CwiHHFZFnHlq6KzjwBX6PhaajHmPm8TPoatugAFWSoxUMP3alkO1K1mzz9bmfdsBmsC8k6\",\n"
            + "  \"plateImage\" : \"/9j/4AAQSkZJRgABAQIAdgB2AAD/7wAPAAAAAAAAAAAAAAAAAP/bAEMAEQwNDw0LEQ8ODxMSERUaKxwaGBgaNSYoHys/N0JBPjc8O0VOY1RFSV5LOzxWdldeZ2pvcG9DU3qDeWyCY21va//bAEMBEhMTGhcaMxwcM2tHPEdra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra2tra//AABEIBDgHgAMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AMdFshp813JZLaxlNlq3mM8ssgxk8/KV65O0dcDmr97pcYnvothE9tA0ovN3MrqqlwU6AYkUDHTHU81nX1/a300sz6bJvMXlxL9p+SHAwCqhR064zjk06bVpJRO4gC3lxEIZrndkMvfCdASAoJ9uAM07AEcdm2ny3L2Qto2TZat5jNLLIMZPJ2leuTgdcDmnWlvaXVpdyrZxwRxWzPGyTmSbeMD5lz0Jzk7QAD261Hf39rezSzPpsgcxeXEv2n5IcDAKqFHTrjOOTSQ38UCTi2sTDJPAYGzNuRQcbiFIz27scZpAQx2zSWs9wziGCIEeYw+8+MhFHcn9Bya1brRoI2vLZYislrAZRd7smVlVWZSnQDEigemOp5rJvLg3jIGjEUEQ2wwKfljH9SepPc1am1aSUTuIAt5cRCGa53ZDL3wnQEgKCfbgDNMRdudLs4ZL+OO3RYoLX\""
            + "}";

    private void processJSON(String json) {
        String dispNewImage = "";
        char a = 'a';
        char b = 'b';
        try {            
            JSONObject obj = new JSONObject(json);
            org.json.JSONArray arr = obj.getJSONArray("parkingSession");
            int status = obj.getInt("status");
            System.out.println("{");
            System.out.println("  \"status\" : " + status);
            String statusMessage = obj.getString("statusMessage");
            System.out.println("  \"statusMessage\" : \"" + statusMessage + "\",");
            System.out.println("  \"parkingSession\" : [ {");
            for (int i = 0; i < arr.length(); i++) {
                System.out.println("    \"id\" : " + arr.getJSONObject(i).getInt("id") + ",");
                System.out.println("    \"uuid\" : \"" + arr.getJSONObject(i).getString("uuid") + "\",");
                System.out.println("    \"car_park_id\" : " + arr.getJSONObject(i).getInt("car_park_id") + ",");
                System.out.println("    \"vehicle_plate_no\" : \"" + arr.getJSONObject(i).getString("vehicle_plate_no") + "\",");
                System.out.println("    \"vehicle_id\" : \"" + arr.getJSONObject(i).getInt("vehicle_id") + "\",");
                System.out.println("    \"vehicle_type\" : \"" + arr.getJSONObject(i).getString("vehicle_type") + "\",");
                System.out.println("    \"entry_date_time\" : \"" + arr.getJSONObject(i).getString("entry_date_time") + "\",");
                System.out.println("    \"entry_session_id\" : \"" + arr.getJSONObject(i).getString("entry_session_id") + "\",");
                System.out.println("    \"entry_type\" : \"" + arr.getJSONObject(i).getString("entry_type") + "\",");
                if (arr.getJSONObject(i).get("exit_date_time").toString().equals("null")) {
                    System.out.println("    \"exit_date_time\" : null,");
                } else {
                    System.out.println("    \"exit_date_time\" : \"" + arr.getJSONObject(i).getJSONObject("exit_date_time") + "\",");
                }
                if (arr.getJSONObject(i).get("exit_session_id").toString().equals("null")) {
                    System.out.println("    \"exit_session_id\" : null,");
                } else {
                    System.out.println("    \"exit_session_id\" : \"" + arr.getJSONObject(i).getJSONObject("exit_session_id") + "\",");
                }
                if (arr.getJSONObject(i).get("exit_type").toString().equals("null")) {
                    System.out.println("    \"exit_type\" : null,");
                } else {
                    System.out.println("    \"exit_type\" : \"" + arr.getJSONObject(i).getJSONObject("exit_type") + "\",");
                }
                if (arr.getJSONObject(i).get("expiry_date_time").toString().equals("null")) {
                    System.out.println("    \"expiry_date_time\" : null,");
                } else {
                    System.out.println("    \"expiry_date_time\" : \"" + arr.getJSONObject(i).getJSONObject("expiry_date_time") + "\",");
                }
                if (arr.getJSONObject(i).get("allow_open").toString().equals("null")) {
                    System.out.println("    \"allow_open\" : null,");
                } else {
                    System.out.println("    \"allow_open\" : \"" + arr.getJSONObject(i).getInt("allow_open") + "\",");
                }
                if (arr.getJSONObject(i).get("ref_id").toString().equals("null")) {
                    System.out.println("    \"ref_id\" : null,");
                } else {
                    System.out.println("    \"ref_id\" : \"" + arr.getJSONObject(i).getJSONObject("ref_id") + "\",");
                }
                if (arr.getJSONObject(i).get("currency_code").toString().equals("null")) {
                    System.out.println("    \"currency_code\" : null,");
                } else {
                    System.out.println("    \"currency_code\" : \"" + arr.getJSONObject(i).getJSONObject("currency_code") + "\",");
                }
                if (arr.getJSONObject(i).get("credit_amount").toString().equals("null")) {
                    System.out.println("    \"credit_amount\" : null,");
                } else {
                    System.out.println("    \"credit_amount\" : \"" + arr.getJSONObject(i).getInt("credit_amount") + "\",");
                }
                if (arr.getJSONObject(i).get("payment_amount").toString().equals("null")) {
                    System.out.println("    \"payment_amount\" : null,");
                } else {
                    System.out.println("    \"payment_amount\" : \"" + arr.getJSONObject(i).getInt("payment_amount") + "\",");
                }
                if (arr.getJSONObject(i).get("transaction_amount").toString().equals("null")) {
                    System.out.println("    \"transaction_amount\" : null,");
                } else {
                    System.out.println("    \"transaction_amount\" : \"" + arr.getJSONObject(i).getInt("transaction_amount") + "\",");
                }
                if (arr.getJSONObject(i).get("duration").toString().equals("null")) {
                    System.out.println("    \"duration\" : null,");
                } else {
                    System.out.println("    \"duration\" : \"" + arr.getJSONObject(i).getInt("duration") + "\",");
                }
                if (arr.getJSONObject(i).get("driver_code").toString().equals("null")) {
                    System.out.println("    \"driver_code\" : null,");
                } else {
                    System.out.println("    \"driver_code\" : \"" + arr.getJSONObject(i).getJSONObject("driver_code") + "\",");
                }
                if (arr.getJSONObject(i).get("noti_contact").toString().equals("null")) {
                    System.out.println("    \"noti_contact\" : null,");
                } else {
                    System.out.println("    \"noti_contact\" : \"" + arr.getJSONObject(i).getJSONObject("noti_contact") + "\",");
                }
                if (arr.getJSONObject(i).get("parking_type_id").toString().equals("null")) {
                    System.out.println("    \"parking_type_id\" : null,");
                } else {
                    System.out.println("    \"parking_type_id\" : \"" + arr.getJSONObject(i).getInt("parking_type_id") + "\",");
                }
                if (arr.getJSONObject(i).get("transaction_type").toString().equals("null")) {
                    System.out.println("    \"transaction_type\" : null,");
                } else {
                    System.out.println("    \"transaction_type\" : \"" + arr.getJSONObject(i).getInt("transaction_type") + "\",");
                }
                if (arr.getJSONObject(i).get("transaction_id").toString().equals("null")) {
                    System.out.println("    \"transaction_id\" : null,");
                } else {
                    System.out.println("    \"transaction_id\" : \"" + arr.getJSONObject(i).getJSONObject("transaction_id") + "\",");
                }
                if (arr.getJSONObject(i).get("company_code").toString().equals("null")) {
                    System.out.println("    \"company_code\" : null,");
                } else {
                    System.out.println("    \"company_code\" : \"" + arr.getJSONObject(i).getJSONObject("company_code") + "\",");
                }
                if (arr.getJSONObject(i).get("status").toString().equals("null")) {
                    System.out.println("    \"status\" : null,");
                } else {
                    System.out.println("    \"status\" : \"" + arr.getJSONObject(i).getString("status") + "\",");
                }
                if (arr.getJSONObject(i).get("created_by").toString().equals("null")) {
                    System.out.println("    \"created_by\" : null,");
                } else {
                    System.out.println("    \"created_by\" : \"" + arr.getJSONObject(i).getString("created_by") + "\",");
                }
                if (arr.getJSONObject(i).get("created_date").toString().equals("null")) {
                    System.out.println("    \"created_date\" : null,");
                } else {
                    System.out.println("    \"created_date\" : \"" + arr.getJSONObject(i).getString("created_date") + "\",");
                }
                if (arr.getJSONObject(i).get("last_modified_by").toString().equals("null")) {
                    System.out.println("    \"last_modified_by\" : null,");
                } else {
                    System.out.println("    \"last_modified_by\" : \"" + arr.getJSONObject(i).getJSONObject("last_modified_by") + "\",");
                }
                if (arr.getJSONObject(i).get("last_modified_date").toString().equals("null")) {
                    System.out.println("    \"last_modified_date\" : null,");
                } else {
                    System.out.println("    \"last_modified_date\" : \"" + arr.getJSONObject(i).getJSONObject("last_modified_date") + "\",");
                }
                if (arr.getJSONObject(i).get("iu_number").toString().equals("null")) {
                    System.out.println("    \"iu_number\" : null,");
                } else {
                    System.out.println("    \"iu_number\" : \"" + arr.getJSONObject(i).getString("iu_number") + "\",");
                }
                if (arr.getJSONObject(i).get("gate_entry_id").toString().equals("null")) {
                    System.out.println("    \"gate_entry_id\" : null,");
                } else {
                    System.out.println("    \"gate_entry_id\" : \"" + arr.getJSONObject(i).getInt("gate_entry_id") + "\",");
                }
                if (arr.getJSONObject(i).get("gate_exit_id").toString().equals("null")) {
                    System.out.println("    \"gate_exit_id\" : null,");
                } else {
                    System.out.println("    \"gate_exit_id\" : \"" + arr.getJSONObject(i).getInt("gate_exit_id") + "\",");
                }
                if (arr.getJSONObject(i).get("parked_exceed_quota").toString().equals("null")) {
                    System.out.println("    \"parked_exceed_quota\" : null,");
                } else {
                    System.out.println("    \"parked_exceed_quota\" : " + arr.getJSONObject(i).getBoolean("parked_exceed_quota") + ",");
                }
                if (arr.getJSONObject(i).get("synced").toString().equals("null")) {
                    System.out.println("    \"synced\" : null,");
                } else {
                    System.out.println("    \"synced\" : " + arr.getJSONObject(i).getBoolean("synced") + ",");
                }

            }
            System.out.println("} ],");
            String intercomImage = obj.getString("intercomImage");
            System.out.println("\"intercomImage\" : \"" + intercomImage + "\",");
            String plateImage = obj.getString("plateImage");
            int min1 = 5;
            int max1 = 21;
            int min2 = 30;
            int max2 = 59;
            int min3 = 67;
            int max3 = 90;
            int min4 = 106;
            int max4 = 1148;
            Random r = new Random();
            
            final String alpha = "AaDEQMS";
            final String beta = "0123456789BCFGHIJKLNOPRTUVWXYZbcdefghijklmnopqrstuvwxyz";
            final int N = alpha.length();
            final int M = beta.length();
            char d = (char)(alpha.charAt(r.nextInt(N)));
            char c = (char)(beta.charAt(r.nextInt(M)));
            a = (char)(alpha.charAt(r.nextInt(N)));
            b = (char)(beta.charAt(r.nextInt(M)));
//            System.out.println("a: " + a);
//            System.out.println("b: " + b);
//            System.out.println("c: " + c);
//            System.out.println("d: " + d);
            int randomNumber = (int) (Math.random() * (max1 - min1)) + min1;
            //if (randomNumber % 2 == 0) {
//            String dispNewImg = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
            dispNewImage = plateImage.substring(1, randomNumber).replace(a + "", b + "") + plateImage.substring(randomNumber, max1);
            randomNumber = (int) (Math.random() * (max2 - min2)) + min2;
            dispNewImage = dispNewImage + plateImage.substring(max1, min2);
            dispNewImage = dispNewImage + plateImage.substring(min2, randomNumber).replace(d + "", c + "") + plateImage.substring(randomNumber, max2);
            randomNumber = (int) (Math.random() * (max3 - min3)) + min3;
            dispNewImage = dispNewImage + plateImage.substring(max2, min3);
            dispNewImage = dispNewImage + plateImage.substring(min3, randomNumber).replace(a + "", b + "") + plateImage.substring(randomNumber, max3);
            randomNumber = (int) (Math.random() * (max4 - min4)) + min4;
            dispNewImage = dispNewImage + plateImage.substring(max3, min4);
            dispNewImage = dispNewImage + plateImage.substring(min4, randomNumber).replace(a + "", "") + plateImage.substring(randomNumber, max4);
            
            dispNewImage = dispNewImage + plateImage.substring(max4, plateImage.length());
            System.out.println("\"plateImage\" : \"/" + dispNewImage + "\"");
            
            System.out.println("}");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ImageDecoderEncoder ide = new ImageDecoderEncoder();
        ide.convertNSaveString2BuffImage("/" +dispNewImage.replace(a + "", b + ""));
    }

    public static void main(String[] args) {
        //inline will store the JSON data streamed in string format
        OnlineAuthConnection t = new OnlineAuthConnection();
//        t.Auth();
//        t.convertString2Image("C:\\NETSHARE\\base64String.txt");
//        t.rollingSnapshots();
        t.processJSON(testResult);
//        String data = "271103104418";
//        t.convertStringToHex(data);
//        t.crc8(t.convertStringToHex(data));
        //t.er = t. Inquire(t.id_token, "TEST_RFID");
//        t.er = t.InquireSpecific(t.id_token, "271103104418F1");
//        t.StringToHexadecimal("271103104418");
//        t.er = t.InquireSpecific(t.id_token, "270122112214" + "10375A");
        //t.er = t.InquireSpecific(t.id_token, "271103162235");
//        t.er = t.postPaymentSuccess(t.id_token, "628c05bd-2167-4c55-a787-709d782afc36", "250", "12345678", "123123", "2022-11-07 13:47:29.000");
        System.exit(0);
    }

}
