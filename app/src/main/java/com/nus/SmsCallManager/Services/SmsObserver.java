package com.nus.SmsCallManager.Services;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.nus.SmsCallManager.Utils.Constants;
import com.nus.SmsCallManager.Utils.TimeUtils;

public class SmsObserver extends ContentObserver {
    private Context context;
    private static final Uri uri = Uri.parse("content://sms");
    private static final String COLUMN_TYPE = "type";
    private static final int MESSAGE_TYPE_RECEIVE = 1;
    private static final int MESSAGE_TYPE_SENT = 2;
    private static int previousSmsId;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
        previousSmsId = getLastSmsId();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            int newId = getLastSmsId();

            if (cursor != null && cursor.moveToFirst() && previousSmsId != newId) {
                int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));
                int lastMsgId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String body = cursor.getString(cursor.getColumnIndex("body")); //content of sms
                    String phoneNumber = cursor.getString(cursor.getColumnIndex("address")); //phone num
                    String time = cursor.getString(cursor.getColumnIndex("date")); //date

                if (type == MESSAGE_TYPE_SENT) {
                    Log.d(Constants.TAG, "Id: " + lastMsgId + "\nSms to: " + phoneNumber + "\nContent: " + body + "\nAt " + TimeUtils.formatTimefromString(time));
                } else if (type == MESSAGE_TYPE_RECEIVE) {
                    Log.d(Constants.TAG, "Id: " + lastMsgId + "\nSms from: " + phoneNumber + "\nContent: " + body + "\nAt: " + TimeUtils.formatTimefromString(time));
                }

                previousSmsId = getLastSmsId();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private int getLastSmsId() {
        Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
        cur.moveToFirst();
        int lastId = cur.getInt(cur.getColumnIndex("_id"));
        return lastId;
    }
}
