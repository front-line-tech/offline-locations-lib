package com.flt.coecclient.service;

import android.content.Intent;

import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import timber.log.Timber;

/**
 * Test FCM settings: https://console.firebase.google.com/project/coec-b0848/settings/cloudmessaging/android:com.flt.coecclient
 * Test using methodology here: https://medium.com/android-school/test-fcm-notification-with-postman-f91ba08aacc3
 * Test Authentication: key=AAAA4pJdMSw:APA91bHC8kP5KjYDYhngIpF3noXcxX8W0dst0L-3XZlL_wuQqgZSvO9b0s_FdtE4-_yQ9e6NPmMjyEplDXMmqm9Iq6yJMDZ9HWsIusAcFhLL8M_ddPhnD55fA6avSsviYScPQeosSMuF
 * Test Content-Type: application/json
 * Test to: cqrOtKUAkoE:APA91bFGmXBPgzlx085GJtmvPHPX8mUaN-SSwx8jNVqwf9OfZ3Nj2FoJaLpeBG2nmIMf1tAEDfwkXmE9i_rdO4TE5sdB0YJWEF_BXVnTA4c3L2ogz9KClen1YhdCHI1-AvxNT8HfaOi2
 */
public class CoecFirebaseMessagingService extends FirebaseMessagingService {

  public static String FIREBASE_MESSAGE_KEY_ACTION = "action";
  public static String FIREBASE_MESSAGE_KEY_SUBJECT_JSON = "subject";
  public static String FIREBASE_MESSAGE_ACTION_NewMicroTasking = "coec.MicroTasking.NEW";

  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    Timber.i("Refreshed token: %s", token);

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // Instance ID token to your app server.

    // FirebaseInstanceId.getInstance().getInstanceId();

  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    // Handle FCM messages here...
    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
    Timber.d("From: %s", remoteMessage.getFrom());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Map<String,String> data = remoteMessage.getData();
      Timber.d("Message data payload: %s", data);

      String action = data.get(FIREBASE_MESSAGE_KEY_ACTION);
      if (FIREBASE_MESSAGE_ACTION_NewMicroTasking.equals(action)) { // it's a new Micro Tasking
        String json = data.get(FIREBASE_MESSAGE_KEY_SUBJECT_JSON);
        CoecMicroTasking tasking = new Gson().fromJson(json, CoecMicroTasking.class);
        Intent intent = CoecService.createIntentWithTasking(this, tasking);
        startService(intent);
      }

    }

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Timber.d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }

}
