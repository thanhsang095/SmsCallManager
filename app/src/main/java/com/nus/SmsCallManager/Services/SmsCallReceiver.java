package com.nus.SmsCallManager.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.nus.SmsCallManager.Utils.Constants;

import java.util.Date;

public class SmsCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;


    @Override
    public void onReceive(Context context, Intent intent) {

        // Listen for incoming && outgoing call
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number == null) {
                number = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
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
        } else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null)
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    str += "SMS from " + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "n";
                }
                //---display the new SMS message---
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Incoming call - goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    // Outgoing call - goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
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
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
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
        Log.d(Constants.TAG, "onIncomingCallStarted - number: " + number);
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(Constants.TAG, "onOutgoingCallStarted - number: " + number);
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(Constants.TAG, "onIncomingCallEnded - number: " + number);
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(Constants.TAG, "onOutgoingCallEnded - number: " + number);
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(Constants.TAG, "onMissedCall - number: " + number);
    }
}
