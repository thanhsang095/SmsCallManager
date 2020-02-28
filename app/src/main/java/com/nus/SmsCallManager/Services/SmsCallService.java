package com.nus.SmsCallManager.Services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.nus.SmsCallManager.Models.CallLogModel;
import com.nus.SmsCallManager.Models.SmsModel;
import com.nus.SmsCallManager.Utils.Constants;
import com.nus.SmsCallManager.Utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class SmsCallService extends Service {
    private static final String SMS_URI = "content://sms";

    private CallObserver receiver = null;
    private ContentResolver contentResolver = null;
    private SmsObserver smsObserver = null;

    public SmsCallService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Register call receiver
        receiver = new CallObserver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(receiver, filter);

        // Register sms observer
        smsObserver = new SmsObserver(new Handler(), this);
        contentResolver = getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse(SMS_URI), true, smsObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG, "Service onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List callLogList = getCallDetails(SmsCallService.this);
                List smsLogList = getMsgDetails(SmsCallService.this);

                String callJson = new Gson().toJson(callLogList);
                String smsJson = new Gson().toJson(smsLogList);

                Log.d(Constants.TAG, "* * * Service Call Details * * * \n" + callJson);
                Log.d(Constants.TAG, "* * * SMS Details * * * \n" + smsJson);
            }
        }).start();
        return Service.START_STICKY;
    }


    /**
     * Get call log details
     *
     * @param context
     * @return
     */
    private List getCallDetails(Context context) {

        List<CallLogModel> callLogList = new ArrayList<>();
        CallLogModel callLogModel;

        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            callLogModel = new CallLogModel();
            callLogModel.setPhoneNumber(managedCursor.getString(number));
            String callDate = managedCursor.getString(date);
            callLogModel.setCallDate(TimeUtils.formatTimefromString(callDate));
            callLogModel.setCallDuration(managedCursor.getString(duration));

            String callTypeNumber = managedCursor.getString(type);
            String callType = null;
            int dircode = Integer.parseInt(callTypeNumber);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;
            }
            callLogModel.setCallType(callType);
            callLogList.add(callLogModel);
        }

        managedCursor.close();
        return callLogList;
    }


    /**
     * Get message inbox details
     *
     * @param context
     * @return
     */
    private List getMsgDetails(Context context) {

        List<SmsModel> smsList = new ArrayList<>();
        SmsModel smsModel;

        Cursor cursor = context.getContentResolver().query(Uri.parse(SMS_URI), null, null, null, null);
        int totalSMS = cursor.getCount();

        if (cursor.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                smsModel = new SmsModel();
                smsModel.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                smsModel.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                smsModel.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                smsModel.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                smsModel.setTime(TimeUtils.formatTimefromString(time));
                if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
                    smsModel.setFolderName("inbox");
                } else {
                    smsModel.setFolderName("sent");
                }

                smsList.add(smsModel);
                cursor.moveToNext();
            }
        } else {
            Log.d(Constants.TAG, "You have no SMS");
        }

        cursor.close();
        return smsList;
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

        if (contentResolver != null && smsObserver != null) {
            contentResolver.unregisterContentObserver(smsObserver);
        }

        Log.d(Constants.TAG, "Service onDestroy");
    }
}
