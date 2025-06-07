package com.example.davide.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showNotificationClicked(View view)
    {
        showNotification();
    }

    protected void showNotification()
    {
        TextView inTxtNotif = findViewById(R.id.inText);
        TextView inTitleNotif = findViewById(R.id.inTitle);
        String txtNotif = inTxtNotif.getText().toString();
        String titleNotif = inTitleNotif.getText().toString();

        if(
                !txtNotif.isEmpty() && !txtNotif.trim().isEmpty()
                && !titleNotif.isEmpty() && !titleNotif.trim().isEmpty()
        ) {
            //---PendingIntent to launch activity if the user selects
            // this notification---

            Intent i = new Intent(this, NotificationView.class);
            i.putExtra("noteID", ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notifBuilder;
            notifBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(titleNotif)
                    .setContentText(txtNotif);
            nm.notify(ID, notifBuilder.build());
        } else {
            Toast.makeText( this, "E` necessario settare il testo e il titolo della notifica!", Toast.LENGTH_SHORT).show();
        }
    }

}
