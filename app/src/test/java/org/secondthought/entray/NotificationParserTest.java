package org.secondthought.entray;

import junit.framework.TestCase;

import org.joda.time.DateTime;

/**
 * Test for NotificationParser.
 *
 * Created by ahogue on 8/7/15.
 */
public class NotificationParserTest extends TestCase {
    private static DateTime morning = new DateTime(2015, 8, 7, 9, 30, 0, 0);
    private static DateTime afternoon = new DateTime(2015, 8, 7, 15, 30, 0, 0);

    private void testOne(DateTime now, String input, String notification, DateTime parsedTime) {
        ParsedNotification parsed = org.secondthought.entray.NotificationParser.parse(input, now);
        assertEquals(notification, parsed.getNotificationText());
        assertEquals(parsedTime, parsed.getDateTime());
    }

    public void testParseNoDate() {
        testOne(afternoon, "Buy milk", "Buy milk", null);
        testOne(afternoon, " Buy milk ", "Buy milk", null);
    }

    public void testParseWithTime() {
        testOne(afternoon, "Buy milk 830pm",   "Buy milk", new DateTime(2015, 8, 7, 20, 30, 0));
        testOne(afternoon, "Buy milk 8:30pm",  "Buy milk", new DateTime(2015, 8, 7, 20, 30, 0));
        testOne(afternoon, "Buy milk 17:30",   "Buy milk", new DateTime(2015, 8, 7, 17, 30, 0));
        testOne(afternoon, "Buy milk 7:30",    "Buy milk", new DateTime(2015, 8, 7, 19, 30, 0));
        testOne(morning,   "Buy milk 11:30",   "Buy milk", new DateTime(2015, 8, 7, 11, 30, 0));
        testOne(morning,   "Buy milk 11:30pm", "Buy milk", new DateTime(2015, 8, 7, 23, 30, 0));
        testOne(morning,   "Buy milk 2:30pm",  "Buy milk", new DateTime(2015, 8, 7, 14, 30, 0));
        testOne(morning,   "Buy milk 2:30",    "Buy milk", new DateTime(2015, 8, 7, 14, 30, 0));
        testOne(morning,   "Buy milk 8:30am",  "Buy milk", new DateTime(2015, 8, 8,  8, 30, 0));
        testOne(afternoon, "Buy milk 7:30",    "Buy milk", new DateTime(2015, 8, 7, 19, 30, 0));
        testOne(afternoon, "Buy milk 7:30am",  "Buy milk", new DateTime(2015, 8, 8,  7, 30, 0));
        testOne(afternoon, "Buy milk 730am",   "Buy milk", new DateTime(2015, 8, 8,  7, 30, 0));
        testOne(afternoon, "Buy milk 8pm",     "Buy milk", new DateTime(2015, 8, 7, 20,  0, 0));
        testOne(afternoon, "Buy milk 8am",     "Buy milk", new DateTime(2015, 8, 8,  8,  0, 0));
        testOne(afternoon, "Buy milk 1pm",     "Buy milk", new DateTime(2015, 8, 8, 13,  0, 0));
        testOne(afternoon, "Buy milk 3:45",    "Buy milk", new DateTime(2015, 8, 7, 15, 45, 0));
        testOne(morning,   "Buy milk 12pm",    "Buy milk", new DateTime(2015, 8, 7, 12,  0, 0));
        testOne(morning,   "Buy milk 12am",    "Buy milk", new DateTime(2015, 8, 8,  0,  0, 0));
    }

    public void testAt() {
        testOne(afternoon, "Buy milk at 1pm",   "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
        testOne(afternoon, "Buy milk @1pm",     "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
        testOne(afternoon, "Buy milk @ 1pm",    "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
    }

    public void testTomorrow() {
        testOne(afternoon, "Buy milk 830pm tomorrow",   "Buy milk", new DateTime(2015, 8, 8, 20, 30, 0));
        testOne(afternoon, "Buy milk 8:30pm tomorrow",  "Buy milk", new DateTime(2015, 8, 8, 20, 30, 0));
        testOne(afternoon, "Buy milk 17:30 tomorrow",   "Buy milk", new DateTime(2015, 8, 8, 17, 30, 0));
        testOne(afternoon, "Buy milk 7:30 tomorrow",    "Buy milk", new DateTime(2015, 8, 8, 7,  30, 0));
        testOne(morning,   "Buy milk 11:30 tomorrow",   "Buy milk", new DateTime(2015, 8, 8, 11, 30, 0));
        testOne(morning,   "Buy milk 11:30pm tomorrow", "Buy milk", new DateTime(2015, 8, 8, 23, 30, 0));
        testOne(morning,   "Buy milk 2:30pm tomorrow",  "Buy milk", new DateTime(2015, 8, 8, 14, 30, 0));
        testOne(morning,   "Buy milk 2:30 tomorrow",    "Buy milk", new DateTime(2015, 8, 8, 2,  30, 0));
        testOne(morning,   "Buy milk 8:30am tomorrow",  "Buy milk", new DateTime(2015, 8, 8, 8,  30, 0));
        testOne(afternoon, "Buy milk 7:30 tomorrow",    "Buy milk", new DateTime(2015, 8, 8, 7,  30, 0));
        testOne(afternoon, "Buy milk 7:30am tomorrow",  "Buy milk", new DateTime(2015, 8, 8, 7,  30, 0));
        testOne(afternoon, "Buy milk 730am tomorrow",   "Buy milk", new DateTime(2015, 8, 8, 7,  30, 0));
        testOne(afternoon, "Buy milk 8pm tomorrow",     "Buy milk", new DateTime(2015, 8, 8, 20, 0,  0));
        testOne(afternoon, "Buy milk 8am tomorrow",     "Buy milk", new DateTime(2015, 8, 8, 8,  0,  0));
        testOne(afternoon, "Buy milk 1pm tomorrow",     "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
    }

    public void testColon() {
        testOne(afternoon, "1pm: Buy milk",           "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
        testOne(afternoon, "1pm tomorrow: Buy milk",  "Buy milk", new DateTime(2015, 8, 8, 13, 0,  0));
    }
}
