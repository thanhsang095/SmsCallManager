package com.nus.SmsCallManager.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String getCurrentTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD-MM-YYYY HH:MM:SS", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

}
