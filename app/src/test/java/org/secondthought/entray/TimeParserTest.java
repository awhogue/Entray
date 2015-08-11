package org.secondthought.entray;

import junit.framework.TestCase;

import org.joda.time.DateTime;

/**
 * Created by ahogue on 8/7/15.
 */
public class TimeParserTest extends TestCase {
    private static DateTime now = new DateTime(2015, 8, 7, 15, 30, 0, 0);

    public void testParse() {
        assertEquals(TimeParser.parse("Buy milk 830pm", now), new DateTime(2015, 8, 7, 20, 30, 0));
        assertEquals(TimeParser.parse("Buy milk 8:30pm", now), new DateTime(2015, 8, 7, 20, 30, 0));
        assertEquals(TimeParser.parse("Buy milk 17:30", now), new DateTime(2015, 8, 7, 17, 30, 0));
        assertEquals(TimeParser.parse("Buy milk 7:30", now), new DateTime(2015, 8, 8, 7, 30, 0));
        assertEquals(TimeParser.parse("Buy milk 730", now), new DateTime(2015, 8, 8, 7, 30, 0));
        assertEquals(TimeParser.parse("Buy milk 8pm", now), new DateTime(2015, 8, 7, 20, 0, 0));
        assertEquals(TimeParser.parse("Buy milk 8am", now), new DateTime(2015, 8, 8, 8, 0, 0));
        assertEquals(TimeParser.parse("Buy milk 1pm", now), new DateTime(2015, 8, 8, 13, 0, 0));
    }
}
