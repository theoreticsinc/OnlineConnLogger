/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import misc.XMLreader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author theor
 */
public class TestOnlineConn {
    
    private static final Logger log = LogManager.getLogger(TestOnlineConn.class);
    
    public boolean wait4Server = false;
    public Calendar cal = Calendar.getInstance();
    private int startYear, startMonth, startDate, startHour, startMinute, startSeconds;
        
    public TestOnlineConn() {
     XMLreader xr = new XMLreader();
        try {
            startYear = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startYear"));
            startMonth = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startMonth"));
            startDate = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startDate"));
        
            startHour = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startHour"));
            startMinute = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startMinute"));
            startSeconds = Integer.parseInt(xr.getElementValue("C:/JTerminals/TestOnlineConn/starttime.xml", "startSeconds"));
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        log.debug("Start Debugging...");
        TestOnlineConn toc = new TestOnlineConn();
        toc.startTest();
        
    }
    private void startTest() {
        wait4Server = true;
        cal.set(startYear, startMonth, startDate, startHour, startMinute, startSeconds);  // Need to Add 8 hours to get correct time
        while(true) {
            if (wait4Server  == true) {
                wait4Server = false;
                checkListOfParkers(cal);
//                toc.cal.add(Calendar.MINUTE, -1);
            }
        }
    }

    private void checkListOfParkers(Calendar cal) {

////        CardInput2.setText("012345678");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'");
        
//        Date now = new Date();
        //Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 16);
//        String dateAfter = sdf.format(cal.getTime());  
//        //2022-11-07 13:47:29.000
        String entryDateTime = "2023-03-07T01:30:05.000+00:00";
//        //entryDateTime = "20" + year + "-" + month + "-" + date + "T" + hour + ":" + minute + ":" + seconds + ".000+00:00";
        entryDateTime = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE) + "T" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + ".000+00:00";
//        
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
        java.util.Date dateObj = null;
        try {
            dateObj = sdf.parse(entryDateTime);
        } catch (ParseException ex) {

        }

        String aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
        aRevisedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateObj);
        log.debug(aRevisedDate);
//        System.out.println(aRevisedDate);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-M-dd H:mm:ss");
//        Date dateObj = new Date();
        String formattedDate = sdf1.format(dateObj);
        Integer year = dateObj.getYear() - 100;
        String YY = year.toString();
        Integer month = dateObj.getMonth() + 1;
        String MM = month.toString();
        Integer date = dateObj.getDate();
        String DD = date.toString();
        Integer hour = dateObj.getHours() - 8;
        if (hour < 0) {
            date--;
            hour = 24 + hour;
            DD = date.toString();
        }
        String HH = hour.toString();
        Integer min = dateObj.getMinutes();
        String MN = min.toString();
        Integer sec = dateObj.getSeconds();
        String SS = sec.toString();

        String GATEID = "29";
        String ETX = "EF";

        //String QRvalue = GATEID + yearStr + monthStr + dateStr + hourStr + minStr + secStr + ETX;
        //String MM = now.getMonth() + "";
        //String DD = now.getDate() + "";
        if (month < 10) {
            MM = "0" + month;
        }
        if (date < 10) {
            DD = "0" + date;
        }
        //String YY = now.getYear() - 100 + "";

        //String HH = now.getHours() + "";
        //String MN = now.getMinutes() + "";
        //String SS = now.getSeconds() + "";
        if (hour < 10) {
            HH = "0" + hour;
        }
        if (min < 10) {
            MN = "0" + min;
        }
        if (sec < 10) {
            SS = "0" + sec;
        }

        String CC = "00";

        String cardData = GATEID + CC + YY + MM + DD + HH + MN + SS;

        MD5 md = new MD5();
        String s = new String(cardData);
        String CS = md.md5Hash(s.getBytes());

        cardData = cardData + CS;
//        String CS = "EF";

        log.debug("cardData: " + cardData);

        Date lostdate = cal.getTime();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {

        }

        try {
            OnlineAuthConnection t = new OnlineAuthConnection();
            t.Auth();
            //public EntryRecord postNewTicket(String auth, String amount, String refID, String transactionID, String dateTime) {

                t.er = t.Auth();
                t.SearchVehicle(t.id_token, cardData.toString());
            } catch (Exception ex) {
                log.debug(ex.getMessage());
            }
        try {
            Thread.sleep(2000);
            
            cal.add(Calendar.SECOND, -1);
            wait4Server = true;
            
        } catch (InterruptedException ex) {

        }

    }

}
