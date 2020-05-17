package com.nus.SmsCallManager.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nus.SmsCallManager.Utils.Constants;
import com.nus.SmsCallManager.Utils.TimeUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CallObserver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedPhoneNumber;


    @Override
    public void onReceive(Context context, Intent intent) {

        // Listen for incoming && outgoing call
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number != null) {
                savedPhoneNumber = number;
            }
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }

    // Incoming call - goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    // Outgoing call - goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (number == null) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // Transition of ringing -> offhook are pickups of incoming calls. Nothing done on them
                callStartTime = new Date();
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    onOutgoingCallStarted(context, number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                // Went to idle - this is the end of a call. What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // Ring but no pickup - a miss
                    onMissedCall(context, number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, number, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }

    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d(Constants.TAG, "onIncomingCallStarted - number: " + number + " -- Time: " + TimeUtils.formatTimefromString(String.valueOf(start.getTime())));
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(Constants.TAG, "onOutgoingCallStarted - number: " + number + " -- Time: " + TimeUtils.formatTimefromString(String.valueOf(start.getTime())));
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(Constants.TAG, "onIncomingCallEnded - number: " + number + " -- Duration: " + TimeUtils.getDateDiff(start, end, TimeUnit.SECONDS));
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(Constants.TAG, "onOutgoingCallEnded - number: " + number + " -- Duration: " + TimeUtils.getDateDiff(start, end, TimeUnit.SECONDS));
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(Constants.TAG, "onMissedCall - number: " + number + " -- Time: " + TimeUtils.formatTimefromString(String.valueOf(start.getTime())));
    }
}
