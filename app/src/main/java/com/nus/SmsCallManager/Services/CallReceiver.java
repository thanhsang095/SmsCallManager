package com.nus.SmsCallManager.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nus.SmsCallManager.Utils.Constants;

import java.util.Date;

public class CallReceiver extends SmsCallReceiver {
    public CallReceiver() {
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onIncomingCallStarted - number: " + number, Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, "onIncomingCallStarted - number: " + number);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onOutgoingCallStarted - number: " + number, Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, "onOutgoingCallStarted - number: " + number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onIncomingCallEnded - number: " + number, Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, "onIncomingCallEnded - number: " + number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onOutgoingCallEnded - number: " + number, Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, "onOutgoingCallEnded - number: " + number);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onMissedCall - number: " + number, Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, "onMissedCall - number: " + number);
    }
}
