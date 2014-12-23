package org.secondthought.entray;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by ahogue on 12/22/14.
 */
public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_TEXT = "notification-text";
    public static String NOTIFICATION_DELAY = "notification-delay";

    private static Random rand = new Random();

    private PendingIntent makePostponeIntent(Context context, int delay, String text, int id) {
        Intent postponeIntent = new Intent(context, NotificationPublisher.class);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_DELAY, delay);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_TEXT, text);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        return PendingIntent.getBroadcast(context, rand.nextInt(), postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra(NOTIFICATION_TEXT);
        int delay = intent.getIntExtra(NOTIFICATION_DELAY, 0);
        int id = intent.getIntExtra(NOTIFICATION_ID, rand.nextInt());
        Toast.makeText(context, "Notification " + id + " got delay " + delay, Toast.LENGTH_LONG).show();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);

        if (0 == delay) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_action_accept)
                            .setContentTitle("Entray")
                            .setContentText(text)
                            .addAction(R.drawable.ic_action_alarms, "1 hour", makePostponeIntent(context, 60*60*1000, text, id))
                            .addAction(R.drawable.ic_action_alarms, "1 day", makePostponeIntent(context, 24*60*60*1000, text, id));

            notificationManager.notify(id, mBuilder.build());
        } else {
            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TEXT, text);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, rand.nextInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long futureInMillis = SystemClock.elapsedRealtime() + delay;
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        }
    }

}
