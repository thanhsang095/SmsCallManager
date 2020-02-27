package com.nus.SmsCallManager.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;

import com.nus.SmsCallManager.Utils.Constants;

import java.util.Date;

public class SmsCallService extends Service {
    CallReceiver receiver = null;

    public SmsCallService() { }

    @Override
    public void onCreate() {
        super.onCreate();
        // init and register receiver
//        receiver = new CallReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        registerReceiver(receiver, filter);
//
//        HandlerThread thread = new HandlerThread("ServiceStartArguments");
//        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG, "Service onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String callText = getCallDetails(SmsCallService.this);
                    Log.d(Constants.TAG, "Service Call Details: \n" + callText);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                stopSelf();
            }
        }).start();
        return Service.START_STICKY;
    }

    private String getCallDetails(Context context) {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        return sb.toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constants.TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        Log.d(Constants.TAG, "Service onDestroy");
    }
}
