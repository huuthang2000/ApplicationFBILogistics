package com.example.demoapp.firebase;

import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_AUDIO;
import static com.example.demoapp.Utils.Cons.INTENT_CALL_TYPE_VIDEO;
import static com.example.demoapp.Utils.Cons.INTENT_USER;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.demoapp.R;
import com.example.demoapp.model.Users;
import com.example.demoapp.view.activity.chat.InvitationOutgoingActivity;

public class CallsListener {

    public CallsListener() {}

    /**
     * Initiates received video call
     * @param user User calling
     * @param context Application context
     */
    public void initiateVideoCall(Users user, Context context) {
        if (user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
            Log.e("mew", user.getUid());
            Toast.makeText(context, user.getName() + " " + context.getString(R.string.is_not_available), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, InvitationOutgoingActivity.class);
            intent.putExtra(INTENT_USER, user);
            intent.putExtra(INTENT_CALL_TYPE, INTENT_CALL_TYPE_VIDEO);
            context.startActivity(intent);
        }
    }

    /**
     * Initiates received video call
     * @param user User calling
     * @param context Application context
     */
    public void initiateCall(Users user, Context context) {
        if (user.getFcmToken() == null || user.getFcmToken().trim().isEmpty())
            Toast.makeText(context, user.getName() + " " + context.getString(R.string.is_not_available), Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(context, InvitationOutgoingActivity.class);
            intent.putExtra(INTENT_USER, user);
            intent.putExtra(INTENT_CALL_TYPE, INTENT_CALL_TYPE_AUDIO);
            context.startActivity(intent);
        }
    }

}