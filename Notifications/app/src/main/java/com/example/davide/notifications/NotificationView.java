package com.example.davide.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationView extends Activity {

    @Override
    public void onCreate(Bundle savedStateInstance)
    {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.notification);

        Intent i = getIntent();
        //---look up the notification manager service---
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //---cancel the notification that we started---
        nm.cancel(i.getExtras().getInt("ID"));
    }

}
