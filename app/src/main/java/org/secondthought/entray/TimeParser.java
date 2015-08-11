package org.secondthought.entray;

/**
 * Parse times entered in natural language-ish format.
 *
 * Created by ahogue on 8/7/15.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

public class TimeParser {
    private static Pattern timeRegex = Pattern.compile("(?i)(1?\\d):?(\\d\\d)?(am|pm)?");

    /**
     * Attempts to parse a time out of the given input. If no time is found, returns the current time.
     *
     * @param input The input string to parse.
     * @return A datetime based on the parse, or the current time if no time is found.
     */
    public static DateTime parse(String input) {
        return parse(input, new DateTime());
    }

    /**
     * Attempts to parse a time out of the given input. If no time is found, returns the "now" time.
     *
     * @param input The input string to parse.
     * @param now The reference time when this string was input.
     * @return A datetime based on the parse, or the current time if no time is found.
     */
    public static DateTime parse(String input, DateTime now) {
        Matcher m = timeRegex.matcher(input);
        if (m.find()) {
            Integer hour = Integer.parseInt(m.group(1));
            Integer minute = 0;
            if (null != m.group(2)) {
                minute = Integer.parseInt(m.group(2));
            }
            if (null != m.group(3)) {
                String ampm = m.group(3).toLowerCase();
                if (ampm.equals("pm")) {
                    hour += 12;
                }
            }

            DateTime adjusted = now.withHourOfDay(hour).withMinuteOfHour(minute);
            if (adjusted.isBefore(now)) {
                // If the new time is before now, then we looped over a midnight, so we increment the day.
                return adjusted.plusDays(1);
            } else {
                return adjusted;
            }
        } else {
            return now;
        }
    }
}