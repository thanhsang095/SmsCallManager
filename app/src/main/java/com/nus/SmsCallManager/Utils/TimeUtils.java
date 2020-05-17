package com.nus.SmsCallManager.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}
