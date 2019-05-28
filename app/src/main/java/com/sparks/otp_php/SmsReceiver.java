package com.sparks.otp_php;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    Boolean b;
    String abcd,xyz;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
             b=sender.endsWith("040060");  //Just to fetch otp sent from WNRCRP
            String messageBody = smsMessage.getMessageBody();
            abcd=messageBody.replaceAll("[^0-9]","");   // here abcd contains otp
          //  which is in number format
            //Pass on the text to our listener.
            if(b==true) {
                mListener.messageReceived(abcd);  // attach value to interface
            //    object
            }
            else
            {
                Toast.makeText(context, "msg is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}