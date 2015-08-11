package org.secondthought.entray;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;
import java.util.Random;

/**
 * Handle creating notifications.
 *
 * Created by ahogue on 12/22/14.
 */
public class NotificationPublisher extends BroadcastReceiver {
    private static final String TAG = "NotificationPublisher";

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_TEXT = "notification-text";
    public static String NOTIFICATION_DELAY = "notification-delay";
    public static String NOTIFICATION_VIBRATE = "notification-vibrate";

    private static Random rand = new Random();

    /**
     * Schedule a notification to publish after a certain delay.
     *
     * @param context Android context.
     * @param delay How long to wait, in milliseconds.
     * @param text The notification text.
     * @param id An ID for the notification.
     * @return the PendingIntent.
     */
    private PendingIntent makePostponeIntent(Context context, int delay, String text, int id) {
        Intent postponeIntent = new Intent(context, NotificationPublisher.class);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_DELAY, delay);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_TEXT, text);
        postponeIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        return PendingIntent.getBroadcast(context, rand.nextInt(), postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Handle an incoming intent to schedule a notification.
     *
     * Text is expected in the NOTIFICATION_TEXT extra string.
     * Additionally, a delay may be specified using NOTIFICATION_DELAY.
     *
     * @param context Android context.
     * @param intent Android intent.
     */
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "NotificatioParser.onReceive()");

        String text = intent.getStringExtra(NOTIFICATION_TEXT);
        int delay = intent.getIntExtra(NOTIFICATION_DELAY, 0);
        int id = intent.getIntExtra(NOTIFICATION_ID, rand.nextInt());
        boolean vibrate = intent.getBooleanExtra(NOTIFICATION_VIBRATE, false);

        ParsedNotification parsedNotification = NotificationParser.parse(text);
        Log.v(TAG, "Parsed: " + parsedNotification);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);

        if (parsedNotification.hasDateTime()) {
            scheduleFutureNotification(context, id,
                    parsedNotification.getNotificationText(),
                    parsedNotification.getDateTime().getMillis());
        } else if (0 == delay) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Entray")
                            .setContentText(text)
                            .addAction(R.drawable.ic_snooze, "1 hour",
                                    makePostponeIntent(context, 60 * 60 * 1000, text, id))
                            .addAction(R.drawable.ic_snooze, "1 day",
                                    makePostponeIntent(context, 24 * 60 * 60 * 1000, text, id));

            if (vibrate) {
                mBuilder.setVibrate(new long[] {0, 200, 100, 200, 100, 200});
            }

            notificationManager.notify(id, mBuilder.build());
        } else {
            scheduleFutureNotification(context, id, text, System.currentTimeMillis() + delay);
        }
    }

    /**
     * Schedule a notification at a time in the future.
     * @param context Android context.
     * @param id Notification id.
     * @param text The notification text.
     * @param timeInMillis Time to send notification, in millis since the epoch.
     */
    private void scheduleFutureNotification(Context context, Integer id, String text, long timeInMillis) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_TEXT, text);
        notificationIntent.putExtra(NOTIFICATION_ID, id);
        notificationIntent.putExtra(NOTIFICATION_VIBRATE, true);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, rand.nextInt(), notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        DateTime dt = new DateTime(timeInMillis);
        Toast.makeText(context, text + " scheduled for " + dt.toString("E h:mma"), Toast.LENGTH_LONG).show();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent);
    }
}
