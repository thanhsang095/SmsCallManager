package com.nus.SmsCallManager.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    /**
     * Get current time in formatter
     *
     * @param
     * @return formattedDate
     */
    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD-MM-YYYY HH:MM:SS", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    /**
     * Convert string to time formatter
     *
     * @param timeStamp
     * @return formattedDate
     */
    public static String formatTimefromString(String timeStamp) {
        String formattedDate = "";

        if (timeStamp == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date = new Date(Long.valueOf(timeStamp));
            formattedDate = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

}
