package org.secondthought.entray;

import org.joda.time.DateTime;

/**
 * Represents input from the user that has been parsed, e.g. by pulling out a date or time from the input text.
 *
 * Created by ahogue on 8/11/15.
 */
public class ParsedNotification {
    private String original;
    private String notificationText;
    private DateTime dateTime;

    public ParsedNotification(String original, String notificationText, DateTime dateTime) {
        this.original = original;
        this.notificationText = notificationText;
        this.dateTime = dateTime;
    }

    public String toString() {
        return "[" + this.notificationText + "] at " + this.dateTime;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean hasDateTime() {
        return (null != this.dateTime);
    }
}
