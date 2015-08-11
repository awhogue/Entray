package org.secondthought.entray;

import junit.framework.TestCase;

import org.joda.time.DateTime;

/**
 * Test for TimeParser.
 *
 * Created by ahogue on 8/7/15.
 */
public class TimeParserTest extends TestCase {
    private static DateTime now = new DateTime(2015, 8, 7, 15, 30, 0, 0);

    private void testOne(String input, String notification, DateTime parsedTime) {
        ParsedNotification parsed = TimeParser.parse(input, now);
        assertEquals(notification, parsed.getNotificationText());
        assertEquals(parsedTime, parsed.getDateTime());
    }

    public void testParse() {
        testOne("Buy milk 830pm", "Buy milk", new DateTime(2015, 8, 7, 20, 30, 0));
        testOne("Buy milk 8:30pm", "Buy milk", new DateTime(2015, 8, 7, 20, 30, 0));
        testOne("Buy milk 17:30", "Buy milk", new DateTime(2015, 8, 7, 17, 30, 0));
        testOne("Buy milk 7:30", "Buy milk", new DateTime(2015, 8, 8, 7, 30, 0));
        testOne("Buy milk 730", "Buy milk", new DateTime(2015, 8, 8, 7, 30, 0));
        testOne("Buy milk 8pm", "Buy milk", new DateTime(2015, 8, 7, 20, 0, 0));
        testOne("Buy milk 8am", "Buy milk", new DateTime(2015, 8, 8, 8, 0, 0));
        testOne("Buy milk 1pm", "Buy milk", new DateTime(2015, 8, 8, 13, 0, 0));
    }
}
